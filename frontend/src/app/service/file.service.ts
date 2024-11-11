import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Constants } from '../config/constants';
import { environment } from '../../environments/environment';
import { ParamMap } from '@angular/router';

/**
 * Service to fetch and post data to the server
 */
@Injectable()
export class FileService {
  private serverUrl: string;

  constructor(private http: HttpClient) {
    this.serverUrl = environment.apiUrl;
  }

  public findAll$(param?: HttpParams): Observable<Map<String, object>> {
    return this.http.get<Map<String, any>>(this.serverUrl + '/list', {
      params: param,
    });
  }

  public uploadFile(files: FormData) {
    return this.http.post(this.serverUrl + '/upload', files, {
      observe: 'response',
    });
  }

  public uploadChunk(chunk: FormData, header: HttpHeaders) {
    return this.http.post(this.serverUrl + '/upload', chunk, {
      observe: 'response',
      headers: header,
    });
  }

  public downloadFile$(filePath: string): Observable<any> {
    let pathParam = new HttpParams().set('dir', filePath);
    console.log('Request from server: ', this.serverUrl + '/download');
    return this.http.get(this.serverUrl + '/download', {
      observe: 'response',
      params: pathParam,
      responseType: 'blob',
    });
  }

  /**
   * format bytes
   * @param bytes (File size in bytes)
   * @param decimals (Decimals point)
   */
  formatBytes(bytes, decimals) {
    if (bytes === 0) {
      return '0 Bytes';
    }
    const k = 1024;
    const dm = decimals <= 0 ? 0 : decimals || 2;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }
}
