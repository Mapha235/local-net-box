import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Observable } from 'rxjs';
import { SharedService } from '../service/shared/shared.service';
import { FileStatus, UploadService } from '../service/upload.service';

@Component({
  selector: 'upload-dialog',
  templateUrl: './upload-dialog.component.html',
  styleUrls: ['./upload-dialog.component.css'],
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    CommonModule,
    MatIconModule,
  ],
})
export class UploadDialogComponent implements OnInit {
  readonly dialogRef = inject(MatDialogRef<UploadDialogComponent>);
  readonly data = inject(MAT_DIALOG_DATA);
  files: any[] = [];
  fileNames: string[] = [];
  uploadProgress: Observable<FileStatus[]>;
  filesCount: number = 0;

  constructor(
    private uploadService: UploadService,
    private sharedService: SharedService
  ) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    console.log('Closed dialog');
  }

  uploadFiles() {
    for (let file of this.files) {
      this.uploadService.upload(file, this.sharedService.getDir());
    }
    this.close(this.files.length === 0 ? false : true);
  }

  close(uploading: boolean): void {
    this.dialogRef.close(uploading);
  }
  /**
   * on file drop handler
   */
  onFileDropped($event) {
    this.prepareFilesList($event);
  }

  /**
   * handle file from browsing
   */
  fileBrowseHandler($event) {
    const files = $event.target.files;
    this.prepareFilesList(files);
  }

  /**
   * Removes file from files list
   * @param index (File index)
   */
  removeFile(index: number) {
    this.files.splice(index, 1);
  }

  /**
   * Convert Files list to normal array list
   * @param files (Files List)
   */
  prepareFilesList(files: Array<any>) {
    for (const item of files) {
      this.files.push(item);
    }
    this.filesCount = this.files.length;
  }
}
