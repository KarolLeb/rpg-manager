import { routes } from './app.routes';
import { LoadChildrenCallback } from '@angular/router';

describe('appRoutes', () => {
  it('should have routes defined', () => {
    expect(routes).toBeDefined();
    expect(routes.length).toBeGreaterThan(0);
  });

  it('should have a login route', () => {
    const loginRoute = routes.find(r => r.path === 'login');
    expect(loginRoute).toBeDefined();
    expect(loginRoute?.path).toBe('login');
    expect(loginRoute?.path).not.toBe('');
  });

  it('should have a dashboard route with authGuard', () => {
    const dashboardRoute = routes.find(r => r.path === 'dashboard');
    expect(dashboardRoute?.canActivate).toBeDefined();
    expect(dashboardRoute?.path).toBe('dashboard');
    expect(dashboardRoute?.path).not.toBe('');
  });

  it('should load lazy components', async () => {
    for (const route of routes) {
      if (route.loadComponent) {
        const component = await (route.loadComponent as () => Promise<any>)();
        expect(component).toBeDefined();
      }
    }
  });
});
