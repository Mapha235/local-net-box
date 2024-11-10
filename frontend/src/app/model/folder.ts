export class Folder {
  id: string = '';
  name: string = '..';
  size: number;
  lastModifiedDate: string = '';
  parentDir: string = '';
  maxDepth: number = 0;

  setParentDir(parentDir: string) {
    this.parentDir = parentDir;
    return this;
  }

  getAbsolutePath(): string {
    return this.parentDir + '/' + this.name;
  }
}
