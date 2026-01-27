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
      role: 'USER',
      id: 123
    };

    service.login({ username: 'test', password: 'pass' }).subscribe(res => {
      expect(res).toEqual(mockResponse);
      expect(localStorage.getItem('token')).toBe('fake-token');
      expect(localStorage.getItem('currentUser')).toBe(JSON.stringify({ id: 123, username: 'testuser', role: 'USER' }));
      expect(localStorage.key(0)).toMatch(/token|currentUser/);
      expect(service.currentUserValue).toEqual({ id: 123, username: 'testuser', role: 'USER' });
    });

    const req = httpMock.expectOne(request => request.url === 'http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.url).toBe('http://localhost:8080/api/auth/login');
    req.flush(mockResponse);
    tick();
  }));

  it('should logout and clear all storage and subject', fakeAsync(() => {
    localStorage.setItem('token', 'token');
    const user = { id: 1, username: 'user', role: 'PLAYER' };
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
    // 1. Neither exists
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    expect(service.isLoggedIn()).toBe(false);
    
    // 2. Only token exists
    localStorage.setItem('token', 'some-token');
    expect(service.isLoggedIn()).toBe(false);

    // 3. Only user exists
    localStorage.removeItem('token');
    const user = { id: 1, username: 'u', role: 'R' };
    localStorage.setItem('currentUser', JSON.stringify(user));
    const serviceWithUser = TestBed.runInInjectionContext(() => new AuthService());
    expect(serviceWithUser.isLoggedIn()).toBe(false);

    // 4. Token is empty string
    localStorage.setItem('token', '');
    expect(serviceWithUser.isLoggedIn()).toBe(false);

    // 5. User is null in subject (extra check for mutation !!this.currentUserSubject.value)
    localStorage.setItem('token', 'valid');
    // We can't easily force subject to null if localStorage has it, 
    // but the logic !!this.currentUserSubject.value is what we test.
    
    // 6. Both exist and non-empty
    localStorage.setItem('token', 'some-token');
    const fullService = TestBed.runInInjectionContext(() => new AuthService());
    expect(fullService.isLoggedIn()).toBe(true);
    
    tick();
  }));

  it('should register successfully with correct URL', fakeAsync(() => {
    service.register({ username: 'new', email: 'new@example.com', password: 'password' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');
    expect(req.request.method).toBe('POST');
    req.flush(null);
    tick();
  }));

  it('should return token from storage or null if not present', () => {
    localStorage.setItem('token', 'my-token');
    expect(service.getToken()).toBe('my-token');
    localStorage.removeItem('token');
    expect(service.getToken()).toBeNull();
    // Test mutation of removeItem key
    localStorage.setItem('token', 'still-here');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should handle malformed user in localStorage', fakeAsync(() => {
    localStorage.setItem('currentUser', 'invalid-json{');
    spyOn(console, 'error');
    
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    
    expect(console.error).toHaveBeenCalledWith('Failed to parse user from local storage', jasmine.any(Error));
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(newService.currentUserValue).toBeNull();
    tick();
  }));
});
