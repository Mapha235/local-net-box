import { Injectable, NgZone } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private snackbar: MatSnackBar, private zone: NgZone) {}

  showErrorSnackbar(message: string, duration = 4000) {
    this.snackbar.open(message, 'Okay', {
      duration
    });
  }
}
