import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastrService } from 'ngx-toastr';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastr = inject(ToastrService);

  // Zmieniono na tablicę stringów. Jeśli nie ma roles, domyślnie pusta tablica.
  const requiredRoles = (route.data['requiredRoles'] as string[]) || [];

  if (authService.isLoggedIn()) {
    // Jeśli użytkownik jest zalogowany
    const userRole = authService.getUserRoles(); // Pobierz pojedynczą rolę użytkownika

    if (requiredRoles.length > 0) {
      // Jeśli trasa wymaga konkretnych ról
      if (userRole && requiredRoles.includes(userRole)) {
        // Użytkownik ma jedną z wymaganych ról
        return true;
      } else {
        // Użytkownik zalogowany, ale nie ma wymaganej roli
        toastr.warning('Brak uprawnień do dostępu do tej strony.');
        router.navigate(['/access-denied']); // Przekieruj na stronę braku dostępu
        return false;
      }
    } else {
      // Użytkownik jest zalogowany i trasa nie wymaga konkretnej roli (np. dla wszystkich zalogowanych)
      return true;
    }
  } else {
    // Użytkownik nie jest zalogowany
    toastr.warning('Musisz się zalogować, aby uzyskać dostęp do tej strony.');
    router.navigate(['/login']); // Przekieruj na stronę logowania
    return false;
  }
};
