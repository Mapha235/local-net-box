import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, lastValueFrom, Subject } from 'rxjs';
import { FileService } from './file.service';
import { Constants } from '../config/constants';

export interface FileStatus {
  filename: string;
  size: string;
  progress: number;
  hash: string;
  uuid: string;
}

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private uploadStatus = new BehaviorSubject<FileStatus[]>([]);
  uploadProgress$ = this.uploadStatus.asObservable();

  fileStatusArr: FileStatus[] = [];

  constructor(private fileService: FileService) {}

  reset() {
    this.fileStatusArr = [];
  }

  /**
   * Calculates the overall upload progress of all files.
   * @returns Upload progress of all files in %
   */
  getOverallProgress(): number {
    const sum = this.fileStatusArr.reduce(
      (accumulator, currentValue) => accumulator + +currentValue.progress, 0);
    return sum / this.fileStatusArr.length;
  }

  /**
   * Uses the status array to check which files have completed the upload progress.
   * @returns List of files that have finished the upload process
   */
  getCompletedFiles() {
    let completedFiles: string[] = [];
    for (const file of this.fileStatusArr) {
      if (file.progress == 100) completedFiles.push(file.filename);
    }
    return completedFiles;
  }

  /**
   * 
   * @returns Number of files selected for upload
   */
  getTotalFiles() : number {
    return this.fileStatusArr.length;
  }

    /** 
  Divides the file into chunks and uploads it to the server.
  @param file: File
  @return: false if the upload failed.
  */
  async upload(file: File, dest: string) {
    const maxAttempts = 3;
    let attemptNr = 0;

    const fileStatus: FileStatus = {
      filename: file.name,
      size: this.fileService.formatBytes(file.size, 2),
      progress: 0,
      hash: '',
      uuid: '',
    };

    this.fileStatusArr.push(fileStatus);
    this.uploadStatus.next(this.fileStatusArr);

    const chunkSize = Constants.CHUNK_SIZE;
    const totalChunks = Math.ceil(file.size / chunkSize);
    const chunkQueue = new Array(totalChunks)
      .fill(totalChunks)
      .map((_, idx) => idx)
      .reverse();

    while (chunkQueue.length > 0) {
      const chunkId = chunkQueue.pop();
      const begin = chunkId! * chunkSize;
      const chunk = file.slice(begin, begin + chunkSize);

      let formData = new FormData();
      formData.append('file', chunk, file.name);

      const header = new HttpHeaders({
        'X-Chunk-Id': chunkId!.toString(),
        'Chunk-Length': chunk.size.toString(),
        'X-Content-Length': file.size.toString(),
        'X-Content-Name': file.name,
        'X-Total-Chunks': totalChunks.toString(),
        'X-Destination-Dir': dest
      });

      const serverResponse$ = await lastValueFrom(
        this.fileService.uploadChunk(formData, header)
      );

      if (!serverResponse$.ok) {
        chunkQueue.push(chunkId!);
        console.log('Failed to upload chunk: ', chunkId, 'of file ', file.name);
        console.log('Reuploading chunk: ', chunkId, 'of file ', file.name);
        if (attemptNr++ == maxAttempts) return false;
      } else {
        this.fileStatusArr.forEach((value) => {
          if (value.filename === file.name) {
            value.progress = Math.floor(((chunkId! + 1) / totalChunks) * 100);
          }
        });
      }
      this.uploadStatus.next(this.fileStatusArr);
    }
    return true;
  }
}
