import { Injectable, signal } from '@angular/core';
import axios from 'axios';
import { LoginForm } from '../models/login-form';
import { RegisterForm } from '../models/register-form';
import { ToastrService } from 'ngx-toastr';
import { jwtDecode } from 'jwt-decode';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import {
  BehaviorSubject,
  Observable,
  tap,
  catchError,
  throwError,
  filter,
  take,
  map,
} from 'rxjs';
import { AccessToken, DecodedToken } from '../models/access-token';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Stan zalogowania oparty na istnieniu tokena i jego ważności
  isLoggedIn = signal(false); // Nadal używamy sygnału

  private apiUrl = 'http://localhost:8090/api/v1'; // Bazowy URL API do autoryzacji
  private accessTokenSubject: BehaviorSubject<string | null> =
    new BehaviorSubject<string | null>(null);
  public accessToken$: Observable<string | null> =
    this.accessTokenSubject.asObservable();

  private isRefreshingToken = false; // Flaga zapobiegająca wielu odświeżeniom jednocześnie
  private refreshTokenSubject: BehaviorSubject<string | null> =
    new BehaviorSubject<string | null>(null);

  constructor(
    private http: HttpClient, // Wstrzyknięcie HttpClient
    private toastr: ToastrService
  ) {
    this.loadAccessTokenFromLocalStorage();
    // Aktualizuj sygnał isLoggedIn za każdym razem, gdy zmieni się stan Access Tokena
    this.accessToken$.subscribe((token) => {
      this.isLoggedIn.set(!!token && !this.isTokenExpired(token));
    });
  }

  /**
   * Ładuje Access Token z localStorage przy starcie aplikacji.
   * Refresh Token jest przechowywany w HttpOnly cookie, więc nie jest dostępny tutaj.
   */
  private loadAccessTokenFromLocalStorage(): void {
    const storedAccessToken = localStorage.getItem('accessToken'); // Zmieniono klucz na 'accessToken'
    if (storedAccessToken) {
      this.accessTokenSubject.next(storedAccessToken);
    }
  }

  /**
   * Zapisuje tokeny po zalogowaniu lub odświeżeniu.
   * Tylko Access Token jest przechowywany w localStorage.
   * Refresh Token jest zarządzany przez HttpOnly cookie z backendu.
   * @param tokens Obiekt zawierający accessToken i refreshToken.
   */
  private saveTokens(tokens: AccessToken): void {
    localStorage.setItem('accessToken', tokens.accessToken); // Zmieniono klucz na 'accessToken'
    this.accessTokenSubject.next(tokens.accessToken);
    // Refresh Token jest ustawiany przez backend w HttpOnly cookie.
    // Nie musimy go tu ręcznie przechowywać, ani nie powinniśmy, jeśli jest HttpOnly.
  }

  /**
   * Loguje użytkownika do systemu.
   * @param loginForm Obiekt z danymi logowania (username, password).
   * @returns Observable z LoginResponse zawierającym tokeny.
   */
  public login(loginForm: LoginForm): Observable<AccessToken> {
    const payload = {
      username: loginForm.username,
      password: loginForm.password,
    };

    return this.http
      .post<AccessToken>(`${this.apiUrl}/login`, payload, {
        withCredentials: true,
      })
      .pipe(
        tap((tokens) => {
          this.saveTokens(tokens); // Zapisz tokeny po pomyślnym zalogowaniu
          this.toastr.success('Zalogowano pomyślnie!');
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Błąd logowania:', error);
          let errorMessage =
            'Błąd logowania. Sprawdź swoje dane uwierzytelniające.';
          if (error.status === 401) {
            errorMessage = 'Nieprawidłowa nazwa użytkownika lub hasło.';
          }
          this.toastr.error(errorMessage);
          return throwError(() => error);
        })
      );
  }

  /**
   * Rejestruje nowego użytkownika.
   * @param registerForm Obiekt z danymi rejestracji.
   * @returns Observable z odpowiedzią z serwera.
   */
  public register(registerForm: RegisterForm): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, registerForm).pipe(
      tap(() => {
        this.toastr.success('Rejestracja przeprowadzona prawidłowo');
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Błąd rejestracji:', error);
        this.toastr.error('Nie udało się zarejestrować');
        return throwError(() => error);
      })
    );
  }

  /**
   * Usuwa tokeny i wylogowuje użytkownika.
   * Wysyła żądanie wylogowania do backendu, aby unieważnić refresh token.
   */
  public logout(): void {
    localStorage.removeItem('accessToken'); // Zmieniono klucz na 'accessToken'
    this.accessTokenSubject.next(null); // Wyczyść token z Subject
    this.isRefreshingToken = false; // Zresetuj flagę odświeżania
    this.isLoggedIn.set(false); // Ustaw sygnał na false

    // Opcjonalne: Wysyłaj żądanie wylogowania do backendu, aby unieważnić refresh token
    this.http
      .post(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.toastr.info('Zostałeś wylogowany.');
          // router.navigate(['/login']); // Przekierowanie wykonujemy w komponencie logowania
        },
        error: (err) => {
          console.error(
            'Błąd wylogowania na backendzie, ale stan lokalny został wyczyszczony:',
            err
          );
          this.toastr.error(
            'Wystąpił błąd podczas wylogowania, ale zostałeś wylogowany lokalnie.'
          );
          // router.navigate(['/login']); // Przekierowanie wykonujemy w komponencie logowania
        },
      });
  }

  /**
   * Sprawdza stan zalogowania na podstawie Access Tokena.
   * Ta metoda jest teraz głównie dla inicjalizacji, subskrypcja na accessToken$ zarządza stanem isLoggedIn.
   */
  public checkLoginStatus(): void {
    const token = localStorage.getItem('accessToken'); // Zmieniono klucz
    this.isLoggedIn.set(!!token && !this.isTokenExpired(token));
  }

  /**
   * Pobiera aktualny Access Token z BehaviorSubject.
   * @returns Access Token jako string lub null.
   */
  public getAccessToken(): string | null {
    return this.accessTokenSubject.getValue();
  }

  /**
   * Dekoduje token JWT.
   * @param token Token JWT do zdekodowania.
   * @returns Zdekodowany token lub null w przypadku błędu.
   */
  public decodeToken(token: string): DecodedToken | null {
    try {
      const decoded: DecodedToken = jwtDecode(token);
      return decoded;
    } catch (error) {
      console.error('Błąd dekodowania tokena JWT:', error);
      return null;
    }
  }

  /**
   * Pobiera zdekodowany token Access Tokena z localStorage.
   * @returns Zdekodowany token lub null.
   */
  public getDecodedToken(): DecodedToken | null {
    const token = this.getAccessToken(); // Użyj getAccessToken()
    if (token) {
      return this.decodeToken(token);
    }
    return null;
  }

  /**
   * Sprawdza, czy Access Token jest wygasły.
   * @param token Token do sprawdzenia.
   * @returns true, jeśli token wygasł lub jest nieprawidłowy; false w przeciwnym razie.
   */
  public isTokenExpired(token: string): boolean {
    try {
      const decoded: DecodedToken = jwtDecode(token);
      if (!decoded.exp) {
        // Jeśli brak pola 'exp' (expiry), token nie ma daty ważności lub jest źle sformatowany
        return false; // Możesz zdecydować, co robić w takim przypadku (np. traktować jako wygasły)
      }
      const currentTime = Date.now() / 1000; // Czas w sekundach
      // Sprawdź, czy token wygaśnie w ciągu najbliższej minuty (mały bufor)
      return decoded.exp < currentTime + 60;
    } catch (error) {
      console.error('Błąd sprawdzania wygaśnięcia tokena:', error);
      return true; // Jeśli jest błąd, zakładamy, że token jest wygasły lub nieprawidłowy
    }
  }

  /**
   * Pobiera role użytkownika z zdekodowanego tokena.
   * @returns Tablica stringów z rolami lub null.
   */
  public getUserRoles(): string | null {
    const decodedToken = this.getDecodedToken();
    if (decodedToken && decodedToken.userType) {
      // Zakładamy, że pole z rolami to 'roles'
      return decodedToken.userType;
    }
    return null;
  }

  /**
   * Pobiera nazwę użytkownika (username) z zdekodowanego tokena.
   * @returns Nazwa użytkownika jako string lub null.
   */
  public getUsername(): string | null {
    const decodedToken = this.getDecodedToken();
    if (decodedToken && decodedToken.sub) {
      // Standardowo pole 'sub' (subject) zawiera nazwę użytkownika
      return decodedToken.sub; // Często 'sub' to username, ale sprawdź Twój backend.
    }
    return null;
  }

  /**
   * Pobiera ID użytkownika (UUID) z zdekodowanego tokena.
   * @returns ID użytkownika jako string (UUID) lub null.
   */
  public getUserId(): string | null {
    const decodedToken = this.getDecodedToken();
    if (decodedToken && decodedToken.userUuid) {
      // Zakładamy, że backend umieszcza UUID w polu 'userUuid'
      return decodedToken.userUuid;
    }
    // Jeśli UUID jest w polu 'sub', można użyć:
    // if (decodedToken && decodedToken.sub) { return decodedToken.sub; }
    return null;
  }

  /**
   * Odświeża Access Token za pomocą Refresh Tokena.
   * Ta metoda jest wywoływana przez AuthInterceptor, gdy Access Token wygaśnie.
   * @returns Observable z nowymi tokenami (Access Token i Refresh Token).
   */
  public refreshAccessToken(): Observable<AccessToken> {
    if (this.isRefreshingToken) {
      // Jeśli już trwa odświeżanie, poczekaj na wynik istniejącego procesu
      return this.refreshTokenSubject.asObservable().pipe(
        filter((token) => token !== null), // Poczekaj, aż token będzie dostępny
        take(1), // Pobierz tylko jedną wartość
        map((accessToken) => ({ accessToken, refreshToken: '' } as AccessToken)) // Zwróć tylko Access Token (Refresh Token jest w cookie)
      );
    }

    this.isRefreshingToken = true;
    this.refreshTokenSubject.next(null); // Resetuj subject, dopóki nie dostaniemy nowego tokena

    // Wysyłaj żądanie odświeżania tokena. Backend oczekuje Refresh Tokena w HttpOnly cookie.
    // withCredentials: true jest konieczne, aby przeglądarka wysłała cookie.
    return this.http
      .post<AccessToken>(
        `${this.apiUrl}/refresh-token`,
        {},
        { withCredentials: true }
      )
      .pipe(
        tap((tokens) => {
          this.saveTokens(tokens); // Zapisz nową parę tokenów
          this.isRefreshingToken = false;
          this.refreshTokenSubject.next(tokens.accessToken); // Powiadom oczekujące żądania o nowym Access Tokenie
          this.toastr.success('Token odświeżony pomyślnie!');
        }),
        catchError((error: HttpErrorResponse) => {
          this.isRefreshingToken = false;
          this.refreshTokenSubject.next(null); // Odświeżanie nie powiodło się
          console.error('Błąd odświeżania tokena:', error);
          this.toastr.error('Sesja wygasła. Proszę zalogować się ponownie.');
          this.logout(); // Wyloguj użytkownika, jeśli odświeżanie się nie powiodło (refresh token wygasł/jest nieważny)
          return throwError(() => error);
        })
      );
  }
  public hasAnyRole(roles: string[]): boolean {
    const userRole = this.getUserRoles(); // Pobierz pojedynczą rolę użytkownika
    if (!userRole) {
      return false; // Brak roli = brak dostępu
    }
    return roles.includes(userRole); // Sprawdź, czy rola użytkownika jest w tablicy wymaganych ról
  }
}
