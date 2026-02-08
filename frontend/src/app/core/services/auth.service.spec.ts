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

  it('should login and save user/token with correct URL and manage storage correctly', fakeAsync(() => {
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      role: 'USER',
      id: 123
    };

    let result: any;
    service.login({ username: 'test', password: 'pass' }).subscribe(res => result = res);

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
    tick();

    expect(result).toEqual(mockResponse);
    expect(localStorage.getItem('token')).toBe('fake-token');
    expect(localStorage.getItem('currentUser')).toBe(JSON.stringify({ id: 123, username: 'testuser', role: 'USER' }));
    expect(service.currentUserValue).toEqual({ id: 123, username: 'testuser', role: 'USER' });
    
    // Explicitly check for mutations like localStorage.setItem("", ...)
    expect(localStorage.getItem('')).toBeNull();
  }));

  it('should logout and clear all storage and subject and NOT leave empty keys', fakeAsync(() => {
    localStorage.setItem('token', 'token');
    const user = { id: 1, username: 'user', role: 'PLAYER' };
    localStorage.setItem('currentUser', JSON.stringify(user));

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(service.currentUserValue).toBeNull();
    
    // Check that we didn't accidentally remove wrong key or use empty key
    expect(localStorage.length).toBe(0);
    tick();
  }));

  it('should return isLoggedIn true only if both token and user exist and are valid', () => {
    const scenarios = [
      { token: null, user: null, expected: false },
      { token: 'valid', user: null, expected: false },
      { token: null, user: { id: 1 } as any, expected: false },
      { token: '', user: { id: 1 } as any, expected: false },
      { token: 'valid', user: { id: 1 } as any, expected: true }
    ];

    scenarios.forEach(s => {
      localStorage.clear();
      if (s.token !== null) localStorage.setItem('token', s.token);
      if (s.user !== null) localStorage.setItem('currentUser', JSON.stringify(s.user));
      
      // Use runInInjectionContext to create fresh instance for each scenario
      const freshService = TestBed.runInInjectionContext(() => new AuthService());
      expect(freshService.isLoggedIn()).toBe(s.expected, `Failed for token: ${s.token}, user: ${s.user}`);
    });
  });

  it('should handle malformed user in localStorage by clearing it', fakeAsync(() => {
    localStorage.setItem('currentUser', 'invalid-json{');
    localStorage.setItem('token', 'some-token');
    spyOn(console, 'error');

    const newService = TestBed.runInInjectionContext(() => new AuthService());

    expect(console.error).toHaveBeenCalled();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(newService.currentUserValue).toBeNull();
    // Should NOT clear token if only user is malformed? 
    // Actually current implementation clears BOTH if try fails? 
    // Wait, let's check code.
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
