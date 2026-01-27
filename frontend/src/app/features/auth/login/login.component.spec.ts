import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideRouter, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../../core/services/auth.service';
import { of, throwError, delay, switchMap } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService
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
  });

  it('should have invalid form initially', () => {
    expect(component.loginForm.valid).toBeFalse();
    expect(component.loginForm.get('username')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should require username', () => {
    const control = component.loginForm.get('username');
    control?.setValue('');
    expect(control?.hasError('required')).toBeTrue();
  });

  it('should validate username min length', () => {
    const control = component.loginForm.get('username');
    control?.setValue('ab');
    expect(control?.hasError('minlength')).toBeTrue();
    control?.setValue('abc');
    expect(control?.valid).toBeTrue(); // assuming no other validators
  });

  it('should require password', () => {
    const control = component.loginForm.get('password');
    control?.setValue('');
    expect(control?.hasError('required')).toBeTrue();
  });

  it('should validate password min length', () => {
    const control = component.loginForm.get('password');
    control?.setValue('12345');
    expect(control?.hasError('minlength')).toBeTrue();
    control?.setValue('123456');
    expect(control?.valid).toBeTrue();
  });

  it('should enable submit button when form is valid', () => {
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    fixture.detectChanges();
    expect(component.loginForm.valid).toBeTrue();
  });

  it('should disable submit button when loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('button');
    expect(btn.disabled).toBeTrue();
  });

  it('should call login and navigate on success', fakeAsync(() => {
    const loginSpy = spyOn(authService, 'login').and.returnValue(of({ token: 't', username: 'u', role: 'R', id: 1 }).pipe(delay(0)));
    const navigateSpy = spyOn(router, 'navigate');

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    
    tick();

    expect(loginSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
    expect(component.isLoading).toBeFalse();
  }));

  it('should set error on login failure', fakeAsync(() => {
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(0), switchMap(() => throwError(() => ({ error: { message: 'Failed' } })))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    
    tick();

    expect(component.error).toBe('Failed');
    expect(component.isLoading).toBeFalse();
  }));

  it('should set fallback error message on login failure without specific message', fakeAsync(() => {
    spyOn(authService, 'login').and.returnValue(of(null).pipe(delay(0), switchMap(() => throwError(() => new Error('Error')))));

    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onSubmit();
    tick();

    expect(component.error).toBe('Login failed. Please check your credentials.');
    expect(component.isLoading).toBeFalse();
  }));

  it('should not submit if form is invalid', () => {
    const loginSpy = spyOn(authService, 'login');
    component.onSubmit();
    expect(loginSpy).not.toHaveBeenCalled();
  });
});