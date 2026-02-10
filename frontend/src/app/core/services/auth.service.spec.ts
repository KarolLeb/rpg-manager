import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, User } from '../models/auth.model';
import { of } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let store: { [key: string]: string } = {};

  beforeEach(() => {
    store = {};
    spyOn(localStorage, 'getItem').and.callFake(key => store[key] || null);
    spyOn(localStorage, 'setItem').and.callFake((key, value) => store[key] = value);
    spyOn(localStorage, 'removeItem').and.callFake(key => delete store[key]);
    spyOn(localStorage, 'clear').and.callFake(() => store = {});

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
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

    const nextSpy = spyOn((s as any).currentUserSubject, 'next').and.callThrough();

    s.login({ username: 'test', password: 'pass' }).subscribe();

    const req = httpMock.expectOne('/api/auth/login');
    req.flush(mockResponse);
    tick();

    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'fake-token');
    expect(localStorage.setItem).toHaveBeenCalledWith('currentUser', jasmine.any(String));
    expect(nextSpy).toHaveBeenCalledWith(jasmine.objectContaining({ username: 'testuser' }));
    
    expect(localStorage.getItem('token')).toBe('fake-token');
    expect(s.currentUserValue).toEqual({ id: 123, username: 'testuser', role: 'USER' });
  }));

    it('login side effects: should set storage and update subject (strict check)', fakeAsync(() => {
      const s = createService();
      const mockResponse: AuthResponse = {
        token: 'tok',
        username: 'u',
        role: 'USER',
        id: 1
      };
  
      const nextSpy = spyOn((s as any).currentUserSubject, 'next').and.callThrough();
  
      s.login({ username: 'u', password: 'p' }).subscribe();
      const req = httpMock.expectOne('/api/auth/login');     
      req.flush(mockResponse);
      tick();
  
      expect(localStorage.setItem).toHaveBeenCalledWith('token', 'tok');
      expect(localStorage.setItem).toHaveBeenCalledWith('currentUser', jasmine.any(String));
      expect(nextSpy).toHaveBeenCalledWith(jasmine.objectContaining({ username: 'u' }));
      expect(localStorage.getItem('token')).toBe('tok');
    }));
  
    it('logout: should clear all storage AND call subject with null (strict check)', () => {
      const s = createService();
      localStorage.setItem('token', 'token');
      localStorage.setItem('currentUser', JSON.stringify({ id: 1 }));
      
      const nextSpy = spyOn((s as any).currentUserSubject, 'next').and.callThrough();
  
      s.logout();
  
      expect(localStorage.removeItem).toHaveBeenCalledTimes(2);
      expect(localStorage.removeItem).toHaveBeenCalledWith('token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('currentUser');
      expect(localStorage.getItem('token')).toBeNull();
      expect(nextSpy).toHaveBeenCalledWith(null);
      expect(s.currentUserValue).toBeNull();
    });
  it('should return isLoggedIn true only if both token and user exist and are valid', () => {
    const scenarios = [
      { token: null, user: null, expected: false },
      { token: 'valid', user: null, expected: false },
      { token: 'valid', user: undefined as any, expected: false },
      { token: null, user: { id: 1 } as any, expected: false },
      { token: '', user: { id: 1 } as any, expected: false },
      { token: 'valid', user: { id: 1 } as any, expected: true }
    ];

    scenarios.forEach((s, index) => {
      localStorage.clear();
      if (s.token !== null) {
        localStorage.setItem('token', s.token);
      }
      if (s.user !== null) {
        localStorage.setItem('currentUser', JSON.stringify(s.user));
      }
      
      const freshService = TestBed.runInInjectionContext(() => new AuthService());
      
      (localStorage.getItem as jasmine.Spy).calls.reset();
      const result = freshService.isLoggedIn();
      expect(result).toBe(s.expected, `Failed for Scenario ${index}: token=${s.token}, user=${!!s.user}`);
      
      // Specifically check that 'token' key was used to kill StringLiteral mutation
      expect(localStorage.getItem).toHaveBeenCalledWith('token');
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

  it('should return false if token is valid but currentUserSubject is manually set to null', () => {
    const s = createService();
    localStorage.setItem('token', 'valid-token');
    (s as any).currentUserSubject.next(null);
    expect(s.isLoggedIn()).toBeFalse();
  });

  it('should return false if token is empty string', () => {
    const s = createService();
    localStorage.setItem('token', '');
    (s as any).currentUserSubject.next({ id: 1 } as any);
    expect(s.isLoggedIn()).toBeFalse();
  });

  it('should handle malformed user in localStorage by clearing it and logging error', fakeAsync(() => {
    localStorage.setItem('currentUser', 'invalid-json{');
    spyOn(console, 'error');

    const newService = TestBed.runInInjectionContext(() => new AuthService());

    expect(console.error).toHaveBeenCalledWith('Failed to parse user from local storage', jasmine.any(Error));
    expect(localStorage.removeItem).toHaveBeenCalledWith('currentUser');
    expect(localStorage.getItem('currentUser')).toBeNull();
    tick();
  }));

  it('should use correct API URL for login and register and check exact string', fakeAsync(() => {
    const s = createService();
    s.login({ username: 'u', password: 'p' }).subscribe();
    // This MUST be exactly the full URL to kill mutations on apiUrl
    const req1 = httpMock.expectOne(req => req.url === '/api/auth/login');
    expect(req1.request.url).toBe('/api/auth/login');
    req1.flush({});

    s.register({ username: 'u', email: 'e', password: 'p' }).subscribe();
    const req2 = httpMock.expectOne(req => req.url === '/api/auth/register');
    expect(req2.request.url).toBe('/api/auth/register');
    req2.flush({});
    
    tick();
  }));

  it('should return token from storage and use correct key', () => {
    const s = createService();
    localStorage.setItem('token', 'my-token');
    expect(s.getToken()).toBe('my-token');
    expect(localStorage.getItem).toHaveBeenCalledWith('token');
  });
});
