import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserProfileDetailsDto } from '../../models/user-profile-details-dto';
import { AuthService } from '../../services/auth.service';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { TranslateModule } from '@ngx-translate/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-userprofile-dashboard',
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatListModule,
    MatIconModule,
    TranslateModule,
    ToastrModule,
  ],
  templateUrl: './userprofile-dashboard.component.html',
  styleUrl: './userprofile-dashboard.component.css',
})
export class UserprofileDashboardComponent {
  userProfile: UserProfileDetailsDto | null = null;
  isLoading: boolean = true;
  error: string | null = null;

  constructor(
    private userService: UserService,
    private toastService: ToastrService,
    private authService: AuthService, // Wstrzyknięcie AuthService
    private router: Router // Wstrzyknięcie Router
  ) {}

  ngOnInit(): void {
    const userUuid = this.authService.getUserId(); // Pobieramy UUID z tokena

    if (userUuid) {
      this.loadUserProfile(userUuid);
    } else {
      this.isLoading = false;
      this.error = 'Brak identyfikatora użytkownika. Proszę się zalogować.';
      this.toastService.error(
        'Brak identyfikatora użytkownika. Zaloguj się ponownie.'
      );
      this.router.navigate(['/login']); // Przekieruj na stronę logowania, jeśli nie ma UUID
    }
  }

  async loadUserProfile(uuid: string): Promise<void> {
    // Zwraca Promise<void> implicite
    this.isLoading = true;
    this.error = null;
    try {
      const data = await this.userService.getUserProfileDetails(uuid); // <-- Użycie 'await'
      this.userProfile = data;
      this.isLoading = false;
      this.toastService.success('Pomyślnie załadowano profil użytkownika.');
    } catch (err) {
      console.error('Failed to fetch user profile:', err);
      this.error =
        'Nie udało się załadować profilu użytkownika. Spróbuj ponownie później.';
      this.isLoading = false;
      this.toastService.error('Nie udało się załadować profilu użytkownika.');
    }
  }
}
