import { inject, Injectable, NgZone } from '@angular/core';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DecisionDialogComponent } from '../../decision-dialog/decision-dialog/decision-dialog.component';
import { lastValueFrom } from 'rxjs';

/**
 * Service that provides notifications shown as dialogs or snackbars.
 * This can be used to either notify the user about errors or warn them about large actions they are about to take.
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  readonly dialog = inject(MatDialog);

  constructor(private snackbar: MatSnackBar, private zone: NgZone) {}

  showErrorSnackbar(message: string, duration = 4000) {
    this.snackbar.open(message, 'Okay', {
      duration,
    });
  }

  openDecisionDialog(message: string): Promise<boolean> {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
      data: {
        message: message,
      },
    };

    const dialogRef = this.dialog.open(DecisionDialogComponent, dialogConfig);
    return lastValueFrom(dialogRef.afterClosed());
  }
}
