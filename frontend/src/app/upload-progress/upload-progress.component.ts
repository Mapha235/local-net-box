import { CommonModule, NgFor, NgIf } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Observable, of, reduce, Subject, takeUntil } from 'rxjs';
import { FileStatus, UploadService } from '../service/upload.service';
import { MatIconModule } from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';

@Component({
  selector: 'app-upload-progress',
  standalone: true,
  imports: [
    MatCardModule,
    NgFor,
    NgIf,
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule
  ],
  templateUrl: './upload-progress.component.html',
  styleUrl: './upload-progress.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadProgressComponent implements OnInit {
  @Output() closeEvent = new EventEmitter<boolean>();
  private _destroy$ = new Subject<void>();

  uploadProgress$: Observable<FileStatus[]>;
  collapsed: boolean = true;

  progress: FileStatus[] = [];
  progressBar: number = 0;
  finishedUploading : number = 0;
  totalFiles : number = 0;

  constructor(
    private uploadService: UploadService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.uploadProgress$ = this.uploadService.uploadProgress$;
    this.uploadProgress$.pipe(
      takeUntil(this._destroy$)
    ).subscribe({
      next: (data) => {
        this.progress = data;
        this.progressBar = this.uploadService.getOverallProgress();
        this.finishedUploading = this.uploadService.getCompletedFiles().length;
        this.totalFiles = this.uploadService.getTotalFiles();
        this.changeDetectorRef.detectChanges();
      },
      error: (err) => console.log("Error while showing the upload process: ", err),
      complete: () => console.log("Completed all file uploads.")
    });
  }

  ngOnDestroy(): void {
    console.log('destroy card');
    this.uploadService.reset();
    this._destroy$.next();
  }

  close(){
    this.closeEvent.emit(true);
  }
}
