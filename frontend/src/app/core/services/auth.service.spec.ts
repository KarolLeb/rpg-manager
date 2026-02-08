import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, User } from '../models/auth.model';
import { of } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  function createService(): AuthService {
    return TestBed.inject(AuthService);
  }

  it('should be created', () => {
    const s = createService();
    expect(s).toBeTruthy();
  });

  it('should login and save user/token with correct URL and manage storage correctly', fakeAsync(() => {
    const s = createService();
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      role: 'USER',
      id: 123
    };

    const setSpy = spyOn(localStorage, 'setItem').and.callThrough();
    const nextSpy = spyOn((s as any).currentUserSubject, 'next').and.callThrough();

    s.login({ username: 'test', password: 'pass' }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    req.flush(mockResponse);
    tick();

    expect(setSpy).toHaveBeenCalledWith('token', 'fake-token');
    expect(setSpy).toHaveBeenCalledWith('currentUser', jasmine.any(String));
    expect(nextSpy).toHaveBeenCalledWith(jasmine.objectContaining({ username: 'testuser' }));
    
    expect(localStorage.getItem('token')).toBe('fake-token');
    expect(s.currentUserValue).toEqual({ id: 123, username: 'testuser', role: 'USER' });
  }));

  it('should logout and clear all storage and subject and use correct keys', fakeAsync(() => {
    const s = createService();
    localStorage.setItem('token', 'token');
    localStorage.setItem('currentUser', JSON.stringify({ id: 1 }));
    const removeSpy = spyOn(localStorage, 'removeItem').and.callThrough();
    const nextSpy = spyOn((s as any).currentUserSubject, 'next').and.callThrough();

    s.logout();

    expect(removeSpy).toHaveBeenCalledWith('token');
    expect(removeSpy).toHaveBeenCalledWith('currentUser');
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(nextSpy).toHaveBeenCalledWith(null);
    expect(s.currentUserValue).toBeNull();
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

    const getItemSpy = spyOn(localStorage, 'getItem').and.callThrough();

    scenarios.forEach((s, index) => {
      localStorage.clear();
      if (s.token !== null) {
        localStorage.setItem('token', s.token);
      }
      if (s.user !== null) {
        localStorage.setItem('currentUser', JSON.stringify(s.user));
      }
      
      const freshService = TestBed.runInInjectionContext(() => new AuthService());
      
      getItemSpy.calls.reset();
      const result = freshService.isLoggedIn();
      expect(result).toBe(s.expected, `Failed for Scenario ${index}: token=${s.token}, user=${!!s.user}`);
      
      // Specifically check that 'token' key was used to kill StringLiteral mutation
      expect(getItemSpy).toHaveBeenCalledWith('token');
    });
  });

  it('should return false if token is missing but user is present (explicit check for mutation)', () => {
    localStorage.clear();
    localStorage.setItem('currentUser', JSON.stringify({ id: 1, username: 'test' }));
    const s = TestBed.runInInjectionContext(() => new AuthService());
    expect(s.isLoggedIn()).toBeFalse();
  });

  it('should return false if token is present but user is missing (explicit check for mutation)', () => {
    localStorage.clear();
    localStorage.setItem('token', 'fake-token');
    const s = TestBed.runInInjectionContext(() => new AuthService());
    expect(s.isLoggedIn()).toBeFalse();
  });

  it('should handle malformed user in localStorage by clearing it and logging error', fakeAsync(() => {
    localStorage.setItem('currentUser', 'invalid-json{');
    spyOn(console, 'error');
    const removeSpy = spyOn(localStorage, 'removeItem').and.callThrough();

    const newService = TestBed.runInInjectionContext(() => new AuthService());

    expect(console.error).toHaveBeenCalled();
    expect(removeSpy).toHaveBeenCalledWith('currentUser');
    expect(localStorage.getItem('currentUser')).toBeNull();
    tick();
  }));

  it('should use correct API URL for login and register and check exact string', fakeAsync(() => {
    const s = createService();
    s.login({ username: 'u', password: 'p' }).subscribe();
    // StringLiteral mutation on apiUrl would change the string to "" or something else
    const req1 = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req1.request.url).toBe('http://localhost:8080/api/auth/login');
    req1.flush({});

    s.register({ username: 'u', email: 'e', password: 'p' }).subscribe();
    const req2 = httpMock.expectOne('http://localhost:8080/api/auth/register');
    expect(req2.request.url).toBe('http://localhost:8080/api/auth/register');
    req2.flush({});
    
    tick();
  }));

  it('should return token from storage and use correct key', () => {
    const s = createService();
    const getItemSpy = spyOn(localStorage, 'getItem').and.callThrough();
    localStorage.setItem('token', 'my-token');
    expect(s.getToken()).toBe('my-token');
    expect(getItemSpy).toHaveBeenCalledWith('token');
  });
});
