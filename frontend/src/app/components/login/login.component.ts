import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCard, MatCardActions, MatCardModule } from '@angular/material/card';
import { ToastrService } from 'ngx-toastr'; // ToastrService is used for dependency injection, ToastrModule is for imports
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  standalone: true, // Assuming this is a standalone component
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatCard,
    MatCardModule,
    MatCardActions,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    // ToastrModule should be imported in the main app module (e.g., AppModule or app.config.ts)
    // not directly in feature components, as it provides a service.
    TranslateModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'], // Corrected from styleUrl to styleUrls
})
export class LoginComponent implements OnInit {
  // username: string = ''; // No longer needed, as Reactive Forms manage this
  // password: string = ''; // No longer needed, as Reactive Forms manage this
  isSubmitting = signal(false);
  validationErrors: Array<any> = [];
  loginForm: FormGroup;
  // isLoggedIn = signal(false); // No longer needed, AuthService manages this state

  constructor(
    private fb: FormBuilder,
    public authService: AuthService, // Changed to public if used in template for isLoggedIn
    private router: Router,
    private toastr: ToastrService // Inject ToastrService
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    // Optionally, if user is already logged in, redirect them
    if (this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/home');
    }
  }

  loginAction() {
    this.isSubmitting.set(true); // Indicate that submission is in progress
    this.validationErrors = []; // Clear previous errors

    if (this.loginForm.invalid) {
      // Handle form validation errors on the client side
      this.toastr.error('Proszę wypełnić wszystkie wymagane pola.');
      this.isSubmitting.set(false);
      return;
    }

    const payload = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
    };

    // Correctly subscribe to the Observable returned by authService.login()
    this.authService.login(payload).subscribe({
      next: (response) => {
        // This block executes if login is successful (HTTP status 200 OK)
        console.log('Login successful:', response);
        this.isSubmitting.set(false); // Reset submitting state
        // this.toastr.success('Zalogowano pomyślnie!'); // Toastr success message is already in AuthService
        this.router.navigateByUrl('/home'); // Navigate on success
        window.location.reload();
      },
      error: (error) => {
        // This block executes if login fails (e.g., HTTP status 401 Unauthorized, 400 Bad Request, network error)
        console.error('Login failed in component:', error);
        this.isSubmitting.set(false); // Reset submitting state
        // The Toastr error message is already handled in AuthService's catchError
        // You might want to display more specific validation errors here if the backend returns them
        if (error.status === 400 && error.error && error.error.errors) {
          this.validationErrors = error.error.errors; // Assuming backend returns validation errors in this structure
        }
      },
      complete: () => {
        // Optional: This block executes when the Observable completes (either success or error flow)
        // console.log('Login Observable completed.');
      },
    });
  }
}
