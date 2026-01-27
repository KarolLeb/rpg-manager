import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
  });

  it('should return true if user is logged in', () => {
    authServiceSpy.isLoggedIn.and.returnValue(true);
    const result = TestBed.runInInjectionContext(() => authGuard({} as ActivatedRouteSnapshot, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeTrue();
    expect(result).not.toBeFalse();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should navigate to login if user is not logged in', () => {
    authServiceSpy.isLoggedIn.and.returnValue(false);
    const result = TestBed.runInInjectionContext(() => authGuard({} as ActivatedRouteSnapshot, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeFalse();
    expect(result).not.toBeTrue();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { returnUrl: '/test' } });
    const navigateArgs = routerSpy.navigate.calls.mostRecent().args;
    expect(navigateArgs[0]).toEqual(['/login']);
    expect(navigateArgs[0]).not.toEqual([]);
    expect(navigateArgs[1]?.queryParams?.['returnUrl']).toBe('/test');
    expect(navigateArgs[1]?.queryParams).toEqual({ returnUrl: '/test' });
    expect(navigateArgs[1]?.queryParams).not.toEqual({});
  });
});
