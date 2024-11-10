import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SharedService {
  private dir: string = '';

  setDir(dir: string): void {
    this.dir = dir;
  }
  getDir(): string {
    return this.dir;
  }

  constructor() {}
}
