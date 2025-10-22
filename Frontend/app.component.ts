import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend';

  isLoggedIn$!: Observable<boolean>;
  isAdmin$!: Observable<boolean>;
  username$!: Observable<string>;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.isAdmin$ = this.authService.isAdmin$;
    this.username$ = this.authService.username$;

    this.checkLoginStatus();
  }

  checkLoginStatus(): void {
    this.http.get<{ username: string, role: string }>('https://localhost:8443/api/users/me', { withCredentials: true })
      .subscribe({
        next: (response) => {
          this.authService.updateAuthStatus(true, response.role === 'ROLE_ADMIN', response.username);
          console.log(`User ${response.username} (${response.role === 'ROLE_ADMIN' ? 'Admin' : 'User'}) is logged in.`);
        },
        error: (err) => {
          this.authService.updateAuthStatus(false, false, '');
          console.log('No user logged in or session expired.');
        }
      });
  }

  logout(): void {
    this.http.post('https://localhost:8443/api/users/logout', {}, {
      withCredentials: true,
      responseType: 'text'
    }).subscribe({
      next: () => {
        alert('Logged out successfully!');
        this.authService.updateAuthStatus(false, false, '');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Logout failed:', err);
      }
    });
  }
}