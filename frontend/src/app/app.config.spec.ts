import { appConfig } from './app.config';

describe('appConfig', () => {
  it('should have required providers', () => {
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBeGreaterThan(0);
  });
});
