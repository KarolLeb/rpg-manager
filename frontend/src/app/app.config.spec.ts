import { appConfig } from './app.config';

describe('appConfig', () => {
  it('should have required providers', () => {
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBeGreaterThan(0);
  });

  it('should verify provider count', () => {
    // Current appConfig.providers has 3 elements:
    // 1. provideZoneChangeDetection
    // 2. provideRouter
    // 3. provideHttpClient with interceptors
    expect(appConfig.providers.length).toBe(3);
    expect(appConfig.providers.length).not.toBe(0);
  });
});