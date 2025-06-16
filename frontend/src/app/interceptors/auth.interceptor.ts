// src/app/interceptors/auth.interceptor.ts
import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, take, filter } from 'rxjs/operators';
import { throwError, BehaviorSubject } from 'rxjs';
import { AuthService } from '../services/auth.service';

// Użyj BehaviorSubject poza funkcją, aby zachowywały się jak singletony w kontekście interceptora
const isRefreshing = new BehaviorSubject<boolean>(false);
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService); // Wstrzyknij AuthService

  let request = req;
  const accessToken = authService.getAccessToken();

  // 1. Dodaj Access Token do wszystkich wychodzących żądań (jeśli istnieje)
  if (accessToken) {
    request = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
  }

  // 2. Przechwyć błędy HTTP z odpowiedzi serwera
  return next(request).pipe(
    catchError((error) => {
      // Sprawdzamy, czy to błąd 401 i nie jest to żądanie logowania/odświeżania
      if (
        error.status === 401 &&
        !request.url.includes('/auth/authenticate') && // Unikaj nieskończonej pętli przy błędzie logowania
        !request.url.includes('/auth/refresh') // Unikaj nieskończonej pętli przy błędzie odświeżania
      ) {
        return handle401Error(request, next, authService); // Obsłuż błąd 401
      }
      return throwError(() => error); // Przekaż inne błędy dalej
    })
  );
};

// Funkcja obsługująca błąd 401 (Unauthorized)
function handle401Error(
  request: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService
) {
  if (!isRefreshing.getValue()) {
    isRefreshing.next(true); // Ustaw flagę, że trwa odświeżanie
    refreshTokenSubject.next(null); // Wyczyść subject

    // Wywołaj metodę odświeżania tokena w AuthService
    return authService.refreshAccessToken().pipe(
      switchMap((tokens) => {
        isRefreshing.next(false); // Zresetuj flagę odświeżania
        refreshTokenSubject.next(tokens.accessToken); // Powiadom inne oczekujące żądania o nowym tokenie
        // Ponów oryginalne żądanie z nowym Access Tokenem
        return next(
          request.clone({
            setHeaders: {
              Authorization: `Bearer ${tokens.accessToken}`,
            },
          })
        );
      }),
      catchError((err) => {
        isRefreshing.next(false); // Zresetuj flagę odświeżania
        refreshTokenSubject.next(null); // Odświeżanie nie powiodło się
        // AuthService.logout() zostanie wywołane przez AuthService, które przekieruje na /login
        return throwError(() => err); // Przekaż błąd dalej
      })
    );
  } else {
    // Jeśli odświeżanie już trwa, poczekaj na nowy token, a następnie ponów oryginalne żądanie
    return refreshTokenSubject.pipe(
      filter((token) => token !== null), // Poczekaj, aż nowy token będzie dostępny
      take(1), // Pobierz tylko raz
      switchMap((token) => {
        return next(
          request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`,
            },
          })
        );
      })
    );
  }
}
