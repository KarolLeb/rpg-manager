import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

describe('authGuard', () => {
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let toastServiceSpy: jasmine.SpyObj<ToastService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn'], { currentUserValue: null });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    toastServiceSpy = jasmine.createSpyObj('ToastService', ['error']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ToastService, useValue: toastServiceSpy }
      ]
    });
  });

  it('should return true if user is logged in and no roles required', () => {
    authServiceSpy.isLoggedIn.and.returnValue(true);
    const mockRoute = { data: {} } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeTrue();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should return true if user is logged in and has required role', () => {
    authServiceSpy.isLoggedIn.and.returnValue(true);
    Object.defineProperty(authServiceSpy, 'currentUserValue', { get: () => ({ role: 'ADMIN' }) });
    const mockRoute = { data: { roles: ['ADMIN'] } } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeTrue();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should return false and navigate to dashboard if user has wrong role', () => {
    authServiceSpy.isLoggedIn.and.returnValue(true);
    Object.defineProperty(authServiceSpy, 'currentUserValue', { get: () => ({ role: 'PLAYER' }) });
    const mockRoute = { data: { roles: ['ADMIN'] } } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeFalse();
    expect(toastServiceSpy.error).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect((routerSpy.navigate as jasmine.Spy).calls.argsFor(0)[0]).toEqual(['/dashboard']);
  });

  it('should navigate to login if user is not logged in', () => {
    authServiceSpy.isLoggedIn.and.returnValue(false);
    const mockRoute = { data: {} } as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => authGuard(mockRoute, { url: '/test' } as RouterStateSnapshot));
    expect(result).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { returnUrl: '/test' } });
    
    const args = (routerSpy.navigate as jasmine.Spy).calls.argsFor(0);
    expect(args[0]).toEqual(['/login']);
    expect(args[1].queryParams).toEqual({ returnUrl: '/test' });
    expect(args[1].queryParams.returnUrl).toBe('/test');
  });
});
