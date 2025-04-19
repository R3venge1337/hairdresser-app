import { Component, OnInit, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-header',
  imports: [MatToolbarModule, MatButtonModule, CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  username: string | null = null;
  userRole: string | null = null;

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.checkLoginStatus();

    if (this.authService.isLoggedIn()) {
      const token = localStorage.getItem('token');
      if (token) {
        const decoded: any = jwtDecode(token);
        this.username = decoded.userLogin;
        this.userRole = decoded.userType;
      }
    } else {
      this.username = null;
      this.userRole = null;
    }
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
