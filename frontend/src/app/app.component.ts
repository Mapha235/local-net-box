import { Component, inject } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { Constants } from './config/constants';
import { UploadDialogComponent } from './upload-dialog/upload-dialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = Constants.TitleOfSite;
  subtitle = Constants.SubtitleOfSite;
  uploadInProgress: boolean = false;
  readonly dialog = inject(MatDialog);

  constructor(
    private titleService: Title,
  ) {
    this.titleService.setTitle(this.title);
  }

  public openUploadDialog() {
    this.uploadInProgress = false;
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
      title: 'upload',
      data: {
        bool: this.uploadInProgress,
      },
    };

    const dialogRef = this.dialog.open(UploadDialogComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((filesUploading) => {
      filesUploading = filesUploading ?? false;
      this.uploadInProgress = filesUploading;
    });
  }
}
