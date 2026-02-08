import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { jwtInterceptor } from './jwt.interceptor';
import { AuthService } from '../services/auth.service';
import { of } from 'rxjs';

describe('jwtInterceptor', () => {
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });
  });

  it('should add Authorization header if token exists and ensure it is correct Bearer format', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');
    const req = new HttpRequest('GET', '/test');
    const next: HttpHandlerFn = (r) => {
      expect(r.headers.has('Authorization')).toBeTrue();
      expect(r.headers.get('Authorization')).toBe('Bearer fake-token');
      expect(r.headers.get('Authorization')).not.toBe('Bearer ');
      expect(r.headers.get('Authorization')).not.toBe('fake-token');
      return of();
    };

    TestBed.runInInjectionContext(() => jwtInterceptor(req, next));
  });

  it('should NOT add Authorization header if token is null or empty and ensure no Bearer null is present', () => {
    [null, '', undefined].forEach(token => {
      authServiceSpy.getToken.and.returnValue(token as any);
      const req = new HttpRequest('GET', '/test');
      const next: HttpHandlerFn = (r) => {
        expect(r.headers.has('Authorization')).toBeFalse();
        expect(r.headers.get('Authorization')).toBeNull();
        const authHeader = r.headers.get('Authorization');
        expect(authHeader).not.toContain('Bearer');
        return of();
      };

      TestBed.runInInjectionContext(() => jwtInterceptor(req, next));
    });
  });
});
