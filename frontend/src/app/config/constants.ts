// Angular Modules
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class Constants {
  static TitleOfSite: string = 'Local Vault';
  static SubtitleOfSite: string = 'A Spring Boot, Angular Application';

  // for file upload
  static CHUNK_SIZE: number = 1.0e7;
}
 