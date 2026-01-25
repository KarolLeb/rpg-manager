import { routes } from './app.routes';

describe('appRoutes', () => {
  it('should have routes defined', () => {
    expect(routes).toBeDefined();
    expect(routes.length).toBeGreaterThan(0);
  });

  it('should have a login route', () => {
    const loginRoute = routes.find(r => r.path === 'login');
    expect(loginRoute).toBeDefined();
  });

  it('should have a dashboard route with authGuard', () => {
    const dashboardRoute = routes.find(r => r.path === 'dashboard');
    expect(dashboardRoute?.canActivate).toBeDefined();
  });
});
