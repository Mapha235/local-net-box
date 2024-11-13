import { NgFor, NgIf } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, mergeMap, of, takeWhile } from 'rxjs';
import { FileEntity } from '../model/file';
import { Folder } from '../model/folder';
import { FileService } from '../service/file.service';
import { SharedService } from '../service/shared/shared.service';
import { NotificationService } from '../service/notification/notification.service';

@Component({
  selector: 'file-list.component',
  styleUrl: './file-list.component.css',
  templateUrl: './file-list.component.html',
  standalone: true,
  imports: [MatTableModule, NgFor, NgIf, MatButton, MatIconModule],
})
export class FileListComponent implements OnInit {
  displayedColumns: string[] = [
    'id',
    'name',
    'size',
    'last-modified',
    'actions',
  ];
  clickedRows = new Set<Folder | FileEntity>();

  folders: Folder[] = [];
  files: FileEntity[] = [];

  // files and folders
  dataSource: MatTableDataSource<Folder | FileEntity>;
  currentDir: String[] = [];

  constructor(
    private fileService: FileService,
    private sharedService: SharedService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    let httpParam = this.route.queryParamMap.pipe(
      mergeMap((params) => {
        // Read the current query parameters and create a HttpParams object
        let paramPath: any;
        paramPath = params.get('dir') == null ? '' : params.get('dir');

        this.sharedService.setDir(paramPath);

        if (paramPath !== '') this.currentDir = paramPath.split('/');
        else this.currentDir = [''];

        let httpParams = new HttpParams()
          .set('dir', paramPath)
          .set('files', true)
          .set('folders', true);
        return of(httpParams);
      }),
      mergeMap((param) => {
        // GET request to the server with the given query parameters
        return this.fileService.findAll$(param).pipe(
          catchError((err) => {
            console.log(err);
            this.notificationService.showErrorSnackbar(
              `Error ${err.status}: Directory does not exist`
            );
            this.currentDir.pop();
            this.router.navigate(['/folder'], {
              queryParams: { dir: this.currentDir.join('/') },
            });
            return of({ folders: [], files: [] });
          })
        );
      })
    );

    httpParam.subscribe({
      next: (data) => {
        this.folders = data['folders'].map((folder) => {
          let foldObj = new Folder();
          Object.assign(foldObj, folder);
          return foldObj;
        });

        this.files = data['files'].map((file) => {
          let fileObj = new FileEntity();
          Object.assign(fileObj, file);
          return fileObj;
        });
        // this.dataSource = [...this.folders, ...this.files];
        this.dataSource = new MatTableDataSource([
          ...this.folders,
          ...this.files,
        ]);
        console.log(this.dataSource.data);
      },
    });
  }

  ngOnInit(): void {}

  @HostListener('unloaded')
  ngOnDestroy() {
    this.clickedRows.clear();
    console.log('cleared');
  }

  goTo(path: string);
  goTo(folderId: number);

  goTo(entity) {
    let path: string;

    if (typeof entity === 'number') {
      path = this.currentDir.slice(0, entity + 1).join('/');
    } else if (entity instanceof Folder) {
      path = entity.getAbsolutePath();
    } else if (entity instanceof File) {
      // TODO: implement file viewer and check for supported file types
      return;
    } else return;

    this.router.navigate(['/folder'], {
      queryParams: { dir: path },
    });
  }

  getType(entity: object): string {
    if (entity instanceof Folder) return 'folder';
    else if (entity instanceof FileEntity) return 'file';
    return '';
  }

  download(storageEntity) {
    let downloadInProgress: boolean = true;
    let fileName = storageEntity.name;

    const type = this.getType(storageEntity);
    if (type === 'folder') fileName += '.zip';
    else if (!(type === 'file')) return;

    this.fileService
      .downloadFile$(storageEntity.getAbsolutePath())
      .pipe(takeWhile(() => downloadInProgress))
      .subscribe({
        next: (response) => {
          let blob = response.body;
          let file = new Blob([blob]);
          const data = window.URL.createObjectURL(file);
          const link = document.createElement('a');
          link.href = data;
          link.download = fileName;
          link.click();
        },
        complete: () => {
          downloadInProgress = false;
          console.log('Download completed');
        },
      });
  }

  async delete(storageEntity, i) {
    const name = storageEntity.name;
    const userDecision: boolean =
      await this.notificationService.openDecisionDialog(name);
    if (userDecision) {
      this.fileService.deleteFile$(storageEntity).subscribe({
        error: (err) => {
          this.notificationService.showErrorSnackbar(
            `Could not delete ${name}: ${err}`
          );
        },
        complete: () => {
          this.notificationService.showErrorSnackbar(
            `Moved [${name}] to recycle bin.`
          );
        },
      });
    }
  }

  format(fileSize: number) {
    let size = this.fileService.formatBytes(fileSize, 2);
    return size;
  }
}
