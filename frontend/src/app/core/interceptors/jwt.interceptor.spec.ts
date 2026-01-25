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

  it('should add Authorization header if token exists', () => {
    authServiceSpy.getToken.and.returnValue('fake-token');
    const req = new HttpRequest('GET', '/test');
    const next: HttpHandlerFn = (r) => {
      expect(r.headers.get('Authorization')).toBe('Bearer fake-token');
      return of();
    };

    TestBed.runInInjectionContext(() => jwtInterceptor(req, next));
  });

  it('should NOT add Authorization header if token does not exist', () => {
    authServiceSpy.getToken.and.returnValue(null);
    const req = new HttpRequest('GET', '/test');
    const next: HttpHandlerFn = (r) => {
      expect(r.headers.has('Authorization')).toBeFalse();
      return of();
    };

    TestBed.runInInjectionContext(() => jwtInterceptor(req, next));
  });
});
