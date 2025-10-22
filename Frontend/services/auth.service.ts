import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
 providedIn: 'root'
})
export class AuthService {

 private _isLoggedIn = new BehaviorSubject<boolean>(false);
 private _isAdmin = new BehaviorSubject<boolean>(false);
 private _username = new BehaviorSubject<string>('');

 public readonly isLoggedIn$: Observable<boolean> = this._isLoggedIn.asObservable();
 public readonly isAdmin$: Observable<boolean> = this._isAdmin.asObservable();
 public readonly username$: Observable<string> = this._username.asObservable();

 constructor() { }

 updateAuthStatus(isLoggedIn: boolean, isAdmin: boolean, username: string): void {
 this._isLoggedIn.next(isLoggedIn);
 this._isAdmin.next(isAdmin);
 this._username.next(username);
 }
}