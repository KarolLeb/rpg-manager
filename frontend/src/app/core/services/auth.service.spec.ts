import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, User } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and save user/token with correct URL', fakeAsync(() => {
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      role: 'USER'
    };

    service.login({ username: 'test', password: 'pass' }).subscribe(res => {
      expect(res).toEqual(mockResponse);
      expect(localStorage.getItem('token')).toBe('fake-token');
      expect(localStorage.getItem('currentUser')).toContain('testuser');
      expect(service.currentUserValue).toEqual({ username: 'testuser', role: 'USER' });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
    tick();
  }));

  it('should logout and clear all storage and subject', fakeAsync(() => {
    localStorage.setItem('token', 'token');
    const user = { username: 'user', role: 'PLAYER' };
    localStorage.setItem('currentUser', JSON.stringify(user));
    
    // Trigger constructor to load user
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    expect(newService.currentUserValue).toEqual(user);
    
    newService.logout();
    
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(newService.currentUserValue).toBeNull();
    tick();
  }));

  it('should return isLoggedIn true only if both token and user exist', fakeAsync(() => {
    expect(service.isLoggedIn()).toBeFalse();
    
    localStorage.setItem('token', 'token');
    // Still false because user subject is null
    expect(service.isLoggedIn()).toBeFalse();

    const user: User = { username: 'u', role: 'R' };
    localStorage.setItem('currentUser', JSON.stringify(user));
    
    // Create new instance to trigger constructor logic for localStorage
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    expect(newService.isLoggedIn()).toBeTrue();
    
    // If we logout, it becomes false
    newService.logout();
    expect(newService.isLoggedIn()).toBeFalse();
    tick();
  }));

  it('should register successfully with correct URL', fakeAsync(() => {
    service.register({ username: 'new', email: 'new@example.com', password: 'password' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');
    expect(req.request.method).toBe('POST');
    req.flush(null);
    tick();
  }));

  it('should return token from storage', () => {
    localStorage.setItem('token', 'my-token');
    expect(service.getToken()).toBe('my-token');
  });

  it('should handle malformed user in localStorage', fakeAsync(() => {
    localStorage.setItem('currentUser', 'invalid-json{');
    spyOn(console, 'error');
    
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    
    expect(console.error).toHaveBeenCalled();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(newService.currentUserValue).toBeNull();
    tick();
  }));
});
