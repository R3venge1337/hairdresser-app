import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-access-denied',
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    TranslateModule,
  ],
  templateUrl: './access-denied.component.html',
  styleUrl: './access-denied.component.css',
})
export class AccessDeniedComponent {
  constructor(private router: Router) {}

  /**
   * Nawiguje użytkownika z powrotem do strony logowania.
   */
  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  /**
   * Nawiguje użytkownika do strony głównej lub innej domyślnej strony.
   */
  goToHome(): void {
    this.router.navigate(['/home']); // Załóżmy, że '/home' to Twoja strona główna
  }
}
