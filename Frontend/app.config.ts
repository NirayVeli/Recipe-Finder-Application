import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { LoginComponent } from './pages/login/login.component';
import { UserHomeComponent } from './pages/user-home/user-home.component';
import { AdminHomeComponent } from './pages/admin-home/admin-home.component';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([
      { path: '', component: UserHomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'user-home', component: UserHomeComponent },
      { path: 'admin-home', component: AdminHomeComponent },
      { path: '**', redirectTo: '' }
    ]),
    provideHttpClient(withInterceptorsFromDi()),
  ]
};