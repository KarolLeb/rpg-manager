import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideRouter, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { of, throwError, delay, switchMap } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;
  let router: Router;

  beforeEach(async () => {
    toastServiceSpy = jasmine.createSpyObj('ToastService', ['success', 'error']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService,
        { provide: ToastService, useValue: toastServiceSpy }
      ]
    })
    .compileComponents();
    
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBeNull();
    expect(component.loginForm.get('username')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should have validators on form controls', () => {
    const username = component.loginForm.get('username');
    const password = component.loginForm.get('password');

    username?.setValue('');
    password?.setValue('');
    expect(username?.hasError('required')).toBeTrue();
    expect(password?.hasError('required')).toBeTrue();

    username?.setValue('ab');
    expect(username?.hasError('minlength')).toBeTrue();

    password?.setValue('12345');
    expect(password?.hasError('minlength')).toBeTrue();
  });

  it('should disable submit button when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('button');
    expect(btn.disabled).toBeTrue();
  });

  it('should call login and navigate on success and manage isLoading correctly', fakeAsync(() => {
    const loginSpy = spyOn(authService, 'login').and.returnValue(of({ token: 't', username: 'u', role: 'R', id: 1 }).pipe(delay(10)));
    const navigateSpy = spyOn(router, 'navigate');

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    expect(component.error).toBeNull();
    
    tick(10);

    expect(loginSpy).toHaveBeenCalled();
    expect(toastServiceSpy.success).toHaveBeenCalledWith('Welcome back, u!');
    expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
    expect(component.isLoading).toBeFalse();
  }));

  it('should set error on login failure and manage isLoading correctly', fakeAsync(() => {
    const errorResponse = { error: { message: 'Custom error' } };
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => errorResponse))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    
    tick(10);

    expect(component.error).toBe('Custom error');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Custom error');
    expect(component.isLoading).toBeFalse();
  }));

  it('should use fallback error message on login failure when message is missing', fakeAsync(() => {
    // Error object is empty
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => ({})))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onSubmit();
    tick(10);

    expect(component.error).toBe('Login failed. Please check your credentials.');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Login failed. Please check your credentials.');
    expect(component.isLoading).toBeFalse();
  }));

  it('should use fallback error message on login failure when err.error is null', fakeAsync(() => {
    // err.error is null
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => ({ error: null })))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onSubmit();
    tick(10);

    expect(component.error).toBe('Login failed. Please check your credentials.');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Login failed. Please check your credentials.');
  }));

  it('should use fallback message if err.error.message is empty string', fakeAsync(() => {
    // err.error.message is empty string
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => ({ error: { message: '' } })))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onSubmit();
    tick(10);

    expect(component.error).toBe('Login failed. Please check your credentials.');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Login failed. Please check your credentials.');
  }));

  it('should kill mutation on success message by checking exact string', fakeAsync(() => {
    const user = { token: 't', username: 'specific_user', role: 'R', id: 1 };
    spyOn(authService, 'login').and.returnValue(of(user).pipe(delay(10)));
    spyOn(router, 'navigate');

    component.loginForm.patchValue({ username: 'testuser', password: 'password123' });
    component.onSubmit();
    tick(10);

    expect(toastServiceSpy.success).toHaveBeenCalledWith('Welcome back, specific_user!');
    expect(toastServiceSpy.success).not.toHaveBeenCalledWith('');
  }));

  it('should not submit if form is invalid and should NOT set isLoading to true', () => {
    const loginSpy = spyOn(authService, 'login');
    component.loginForm.controls['username'].setValue('');
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    
    expect(loginSpy).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  });
});