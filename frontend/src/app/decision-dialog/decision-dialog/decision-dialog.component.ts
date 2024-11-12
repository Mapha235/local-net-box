import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';

@Component({
  selector: 'app-decision-dialog',
  standalone: true,
  imports: [MatDialogActions, MatDialogContent, MatButtonModule],
  templateUrl: './decision-dialog.component.html',
  styleUrl: './decision-dialog.component.css',
})
export class DecisionDialogComponent {
  message: string;
  readonly data = inject(MAT_DIALOG_DATA);

  constructor(private dialogRef: MatDialogRef<DecisionDialogComponent>) {
    this.message = this.data.data['message'];
  }

  close(userDecision: boolean): void {
    this.dialogRef.close(userDecision);
  }
}
