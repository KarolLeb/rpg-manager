import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { provideRouter } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule],
      providers: [provideRouter([])]
    })
    .compileComponents();
    
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
    expect(component.registerForm.errors?.['mismatch']).toBeTruthy(); // Wait, validator is on group? No, usually on group but errors might be on group or control depending on implementation.
    // In my code: { validators: this.passwordMatchValidator } -> Logic returns { mismatch: true } on the group.
    
    // Check group error
    expect(component.registerForm.hasError('mismatch')).toBeTruthy();
  });

  it('should be valid when passwords match', () => {
    component.registerForm.controls['username'].setValue('User');
    component.registerForm.controls['email'].setValue('test@example.com');
    component.registerForm.controls['password'].setValue('password123');
    component.registerForm.controls['confirmPassword'].setValue('password123');
    expect(component.registerForm.valid).toBeTruthy();
  });
});
