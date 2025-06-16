import { Component, OnInit, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { jwtDecode } from 'jwt-decode';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    CommonModule,
    RouterLink,
    MatIconModule,
    TranslateModule,
    LanguageSwitcherComponent,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  username: string | null = null;
  userRole: string | null = null;

  constructor(
    public authService: AuthService,
    private router: Router,
    public translateService: TranslateService
  ) {}

  ngOnInit() {
    this.authService.checkLoginStatus();

    if (this.authService.isLoggedIn()) {
      const decodedToken = this.authService.getDecodedToken();
      if (decodedToken) {
        this.username = decodedToken.userLogin;
        this.userRole = decodedToken.userType;
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
