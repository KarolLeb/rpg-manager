import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { provideRouter, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../../core/services/auth.service';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule],
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
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate password mismatch', () => {
    const password = component.registerForm.controls['password'];
    const confirmPassword = component.registerForm.controls['confirmPassword'];
    
    password.setValue('password123');
    confirmPassword.setValue('password456');
    
    expect(component.registerForm.hasError('mismatch')).toBeTrue();
    expect(component.registerForm.errors?.['mismatch']).toBeTrue();
  });

  it('should be valid when passwords match', () => {
    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    expect(component.registerForm.valid).toBeTrue();
    expect(component.registerForm.hasError('mismatch')).toBeFalse();
  });

  it('should have required validators on all fields', () => {
    const username = component.registerForm.get('username');
    const email = component.registerForm.get('email');
    const password = component.registerForm.get('password');
    const confirmPassword = component.registerForm.get('confirmPassword');

    username?.setValue('');
    email?.setValue('');
    password?.setValue('');
    confirmPassword?.setValue('');

    expect(username?.errors?.['required']).toBeTruthy();
    expect(email?.errors?.['required']).toBeTruthy();
    expect(password?.errors?.['required']).toBeTruthy();
    expect(confirmPassword?.errors?.['required']).toBeTruthy();
  });

  it('should validate email format', () => {
    const email = component.registerForm.get('email');
    email?.setValue('invalid-email');
    expect(email?.errors?.['email']).toBeTruthy();
  });

  it('should validate minLength for username and password', () => {
    const username = component.registerForm.get('username');
    const password = component.registerForm.get('password');

    username?.setValue('ab');
    password?.setValue('12345');

    expect(username?.errors?.['minlength']).toBeTruthy();
    expect(password?.errors?.['minlength']).toBeTruthy();
  });

  it('should navigate to login on registration success and manage isLoading', () => {
    const registerSpy = spyOn(authService, 'register').and.returnValue(of(void 0));
    const navigateSpy = spyOn(router, 'navigate');

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    // isLoading is set to true then false synchronously because of 'of(void 0)'
    // To test intermediate state we'd need a more complex setup, but we can test the final state
    
    expect(registerSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login'], { queryParams: { registered: true } });
    expect(component.isLoading).toBeFalse();
  });

  it('should set error on registration failure and manage isLoading', () => {
    const errorResponse = { error: { message: 'Registration failed' } };
    spyOn(authService, 'register').and.returnValue(throwError(() => errorResponse));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    component.onSubmit();

    expect(component.error).toBe('Registration failed');
    expect(component.isLoading).toBeFalse();
  });

  it('should use default error message on registration failure if message is missing', () => {
    spyOn(authService, 'register').and.returnValue(throwError(() => ({ error: {} })));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    component.onSubmit();

    expect(component.error).toBe('Registration failed. Please try again.');
  });

  it('should return early if form is invalid', () => {
    const registerSpy = spyOn(authService, 'register');
    component.registerForm.patchValue({
      username: '', // Invalid
    });
    component.onSubmit();
    expect(registerSpy).not.toHaveBeenCalled();
  });
});
