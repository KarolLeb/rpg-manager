import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { provideRouter, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../../../core/services/auth.service';
import { of, throwError, delay, switchMap } from 'rxjs';

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
    expect(component.isLoading).toBe(false);
    expect(component.error).toBeNull();
    
    // Kill StringLiteral mutants in fb.group
    expect(component.registerForm.get('username')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
    expect(component.registerForm.get('password')?.value).toBe('');
    expect(component.registerForm.get('confirmPassword')?.value).toBe('');
  });

  it('should validate password mismatch', () => {
    const password = component.registerForm.controls['password'];
    const confirmPassword = component.registerForm.controls['confirmPassword'];
    
    password.setValue('password123');
    confirmPassword.setValue('password456');
    
    expect(component.registerForm.hasError('mismatch')).toBe(true);
    expect(component.registerForm.errors?.['mismatch']).toBe(true);
  });

  it('should be invalid when only password is set', () => {
    component.registerForm.get('password')?.setValue('password123');
    component.registerForm.get('confirmPassword')?.setValue('');
    expect(component.registerForm.hasError('mismatch')).toBe(true);
    expect(component.registerForm.errors?.['mismatch']).toBe(true);
  });

  it('should be invalid when passwords do not match', () => {
    component.registerForm.get('password')?.setValue('password123');
    component.registerForm.get('confirmPassword')?.setValue('different');
    expect(component.registerForm.hasError('mismatch')).toBe(true);
    expect(component.registerForm.errors?.['mismatch']).toBe(true);
  });

  it('should be valid when passwords match', () => {
    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    expect(component.registerForm.valid).toBe(true);
    expect(component.registerForm.hasError('mismatch')).toBe(false);
    expect(component.registerForm.errors).toBeNull();
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

  it('should navigate to login on registration success and manage isLoading', fakeAsync(() => {
    const registerSpy = spyOn(authService, 'register').and.returnValue(of(void 0).pipe(delay(1)));
    const navigateSpy = spyOn(router, 'navigate');

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    expect(component.isLoading).toBe(false);
    component.onSubmit();
    expect(component.isLoading).toBe(true);
    
    tick(1);
    
    expect(registerSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login'], { queryParams: { registered: true } });
    const navigateArgs = navigateSpy.calls.mostRecent().args;
    expect(navigateArgs[0]).toEqual(['/login']);
    expect(navigateArgs[1]?.queryParams?.['registered']).toBe(true);
    
    expect(component.isLoading).toBe(false);
  }));

  it('should set error on registration failure and manage isLoading', fakeAsync(() => {
    const errorResponse = { error: { message: 'Registration failed' } };
    spyOn(authService, 'register').and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => errorResponse))));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    expect(component.isLoading).toBe(false);
    component.onSubmit();
    expect(component.isLoading).toBe(true);

    tick(1);

    expect(component.error).toBe('Registration failed');
    expect(component.isLoading).toBe(false);
  }));

  it('should use default error message on registration failure if error object is missing', fakeAsync(() => {
    spyOn(authService, 'register').and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => ({})))));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    component.onSubmit();
    tick(1);

    expect(component.error).toBe('Registration failed. Please try again.');
    expect(component.isLoading).toBe(false);
  }));

  it('should use default error message on registration failure if message is missing in error object', fakeAsync(() => {
    spyOn(authService, 'register').and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => ({ error: {} })))));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    
    component.onSubmit();
    tick(1);

    expect(component.error).toBe('Registration failed. Please try again.');
  }));

  it('should return early if form is invalid', () => {
    const registerSpy = spyOn(authService, 'register');
    component.registerForm.patchValue({
      username: '', // Invalid
    });
    component.onSubmit();
    expect(registerSpy).not.toHaveBeenCalled();
    expect(component.isLoading).toBe(false);
  });

  it('should return mismatch error if confirmPassword is null', () => {
    const validator = component.passwordMatchValidator();
    const group = component.registerForm;
    const originalGet = group.get.bind(group);
    spyOn(group, 'get').and.callFake((name: string) => {
      if (name === 'password') return originalGet('password');
      return null;
    });
    
    expect(validator(group)).toEqual({ mismatch: true });
  });

  it('should return mismatch error if password is null', () => {
    const validator = component.passwordMatchValidator();
    const group = component.registerForm;
    const originalGet = group.get.bind(group);
    spyOn(group, 'get').and.callFake((name: string) => {
      if (name === 'confirmPassword') return originalGet('confirmPassword');
      return null;
    });
    
    expect(validator(group)).toEqual({ mismatch: true });
  });
});
