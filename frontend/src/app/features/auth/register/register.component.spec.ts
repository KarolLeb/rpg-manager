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
    component.registerForm.controls['password'].setValue('password123');
    component.registerForm.controls['confirmPassword'].setValue('password456');
    expect(component.registerForm.hasError('mismatch')).toBeTruthy();
  });

  it('should be valid when passwords match', () => {
    component.registerForm.controls['username'].setValue('User');
    component.registerForm.controls['email'].setValue('test@example.com');
    component.registerForm.controls['password'].setValue('password123');
    component.registerForm.controls['confirmPassword'].setValue('password123');
    expect(component.registerForm.valid).toBeTruthy();
  });

  it('should navigate to login on registration success', () => {
    const registerSpy = spyOn(authService, 'register').and.returnValue(of(void 0));
    const navigateSpy = spyOn(router, 'navigate');

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    component.onSubmit();

    expect(registerSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login'], { queryParams: { registered: true } });
    expect(component.isLoading).toBeFalse();
  });

  it('should set error on registration failure', () => {
    spyOn(authService, 'register').and.returnValue(throwError(() => ({ error: { message: 'Failed' } })));

    component.registerForm.patchValue({
      username: 'User',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    component.onSubmit();

    expect(component.error).toBe('Failed');
    expect(component.isLoading).toBeFalse();
  });

  it('should return early if form is invalid', () => {
    const registerSpy = spyOn(authService, 'register');
    component.onSubmit();
    expect(registerSpy).not.toHaveBeenCalled();
  });
});
