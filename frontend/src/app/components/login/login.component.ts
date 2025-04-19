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
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
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
    ToastrModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  username: string = '';
  password: string = '';
  isSubmitting = signal(false);
  validationErrors: Array<any> = [];
  loginForm: FormGroup;
  isLoggedIn = signal(false);

  constructor(
    private fb: FormBuilder,
    public authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  loginAction() {
    this.isSubmitting.set(true);
    let payload = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
    };
    this.authService.login(payload);
    this.router.navigateByUrl('/home');
  }
}
