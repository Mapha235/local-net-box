export class FileEntity {
  id: string = '';
  name: string = '..';
  size: number;
  lastModifiedDate: string = '';
  parentDir: string = '';
  type: string = 'file';

  setParentDir(parentDir: string) {
    this.parentDir = parentDir;
    return this;
  }

  getAbsolutePath(): string {
    return this.parentDir + '/' + this.name;
  }
}
