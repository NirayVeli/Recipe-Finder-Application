import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  isRegisterMode: boolean = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.username = '';
    this.password = '';
  }

  login(): void {
    const credentials = {
      username: this.username,
      password: this.password
    };

    this.http.post<any>('https://localhost:8443/api/users/login', credentials, {
      withCredentials: true
    }).subscribe({
      next: (user) => {
        this.authService.updateAuthStatus(true, user.role === 'ROLE_ADMIN', user.username);

        if (user && user.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin-home']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        alert('Invalid username or password');
        console.error('Login error:', err);
      }
    });
  }

  register(): void {
    const newUser = {
      username: this.username,
      password: this.password,
      role: 'USER'
    };

    this.http.post('https://localhost:8443/api/users/register', newUser, {
      responseType: 'text'
    }).subscribe({
      next: () => {
        alert('Registration successful! You can now log in.');
        this.toggleMode();
      },
      error: (err) => {
        if (err.status === 409) {
          alert('Username already exists.');
        } else {
          alert('Registration failed.');
        }
        console.error('Registration error:', err);
      }
    });
  }
}