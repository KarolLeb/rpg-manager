import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, User } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and save user/token', () => {
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      role: 'USER'
    };

    service.login({ username: 'test', password: 'pass' }).subscribe(res => {
      expect(res).toEqual(mockResponse);
      expect(localStorage.getItem('token')).toBe('fake-token');
      expect(localStorage.getItem('currentUser')).toContain('testuser');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear storage', () => {
    localStorage.setItem('token', 'token');
    localStorage.setItem('currentUser', JSON.stringify({ username: 'user' }));
    
    service.logout();
    
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(service.currentUserValue).toBeNull();
  });

  it('should return isLoggedIn correctly', () => {
    expect(service.isLoggedIn()).toBeFalse();
    
    localStorage.setItem('token', 'token');
    // We need to re-initialize or mock subject because constructor runs on start
    const user: User = { username: 'u', role: 'R' };
    localStorage.setItem('currentUser', JSON.stringify(user));
    
    // Create new instance to trigger constructor logic for localStorage
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    expect(newService.isLoggedIn()).toBeTrue();
  });

  it('should register successfully', () => {
    service.register({ username: 'new', email: 'new@example.com', password: 'password' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('should return token from storage', () => {
    localStorage.setItem('token', 'my-token');
    expect(service.getToken()).toBe('my-token');
  });

  it('should handle malformed user in localStorage', () => {
    localStorage.setItem('currentUser', 'invalid-json{');
    spyOn(console, 'error');
    
    const newService = TestBed.runInInjectionContext(() => new AuthService());
    
    expect(console.error).toHaveBeenCalled();
    expect(localStorage.getItem('currentUser')).toBeNull();
    expect(newService.currentUserValue).toBeNull();
  });
});
