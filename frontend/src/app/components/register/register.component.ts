import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import {
  MatError,
  MatFormField,
  MatFormFieldControl,
  MatLabel,
} from '@angular/material/form-field';
import { RegisterForm } from '../../models/register-form';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { TranslateModule } from '@ngx-translate/core';
import { ToastrModule, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-register',
  imports: [
    CommonModule,
    MatInputModule,
    MatCardModule,
    MatFormField,
    MatLabel,
    MatError,
    ReactiveFormsModule,
    MatToolbarModule,
    MatButtonModule,
    TranslateModule,
    ToastrModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastService: ToastrService
  ) {
    this.registerForm = this.fb.group({
      firstname: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', Validators.required],
      phoneNumber: ['', Validators.required],
    });
  }

  async onSubmit() {
    const formData: RegisterForm = this.registerForm.value;
    if (this.registerForm.valid) {
      this.authService.register(formData).subscribe({
        next: () => {
          this.router.navigate(['/login']); // Przekieruj na stronę logowania po pomyślnej rejestracji
        },
        error: (error) => {
          // Ten blok wykona się, jeśli rejestracja zakończy się błędem
          console.error('Registration failed:', error);
          // Obsłuż specyficzne błędy rejestracji, jeśli backend je zwraca (np. konflikt)
          if (error.status === 409) {
            // Konflikt, np. nazwa użytkownika/e-mail już istnieje
            this.toastService.error(
              'Użytkownik z podaną nazwą użytkownika lub e-mailem już istnieje.'
            );
          }
        },
      });
    }
  }
}
