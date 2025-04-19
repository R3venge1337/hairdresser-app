import { Injectable, signal } from '@angular/core';
import axios from 'axios';
import { LoginForm } from '../models/login-form';
import { RegisterForm } from '../models/register-form';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  isLoggedIn = signal(false);

  constructor(private toastr: ToastrService) {}

  async login(loginForm: LoginForm): Promise<any> {
    const payload = {
      username: loginForm.username,
      password: loginForm.password,
    };

    try {
      const response = await axios.post('/login', payload);
      const token = response.data.accessToken;
      localStorage.setItem('token', token);
      this.isLoggedIn.set(true);
      this.toastr.success('Udało się zalogować');
      return response.data;
    } catch (error) {
      this.toastr.error('Nie udało sie zalogować');
      console.error('Błąd logowania:', error);
      throw error;
    }
  }

  async register(registerForm: RegisterForm): Promise<any> {
    try {
      const response = await axios.post('/register', registerForm);
      this.toastr.success('Rejestracja przeprowadzona prawidłowo');
    } catch (error) {
      this.toastr.error('Nie udało sie zarejestrować');
      console.error('Błąd rejestracji:', error);
      throw error;
    }
  }

  logout() {
    localStorage.removeItem('token');
    this.isLoggedIn.set(false);
  }

  checkLoginStatus() {
    const token = localStorage.getItem('token');
    this.isLoggedIn.set(!!token);
  }
}
