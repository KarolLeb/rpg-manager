# RpgClient

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.19.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing using Playwright, you have several options:

> [!IMPORTANT]
> Before running end-to-end tests, ensure that the frontend service is not running in Docker (e.g., run `docker-compose down` in the `app-infra/` directory). Playwright is configured to start its own development server for testing.

- `npm run e2e`: Standard test run.
- `npm run e2e:ui`: Open Playwright UI for interactive debugging.
- `npm run e2e:cli`: Recommended for terminal/CLI environments. Uses the `list` reporter and prevents automatic browser/report popups that might freeze the CLI.

```bash
npm run e2e:cli
```

## 🏗️ Backend Integration

The frontend communicates with a **Spring Boot** backend designed using **Hexagonal Architecture**. 

- **API Base:** `http://localhost:8080/api`
- **Authentication:** JWT-based. Ensure you log in via `/api/auth/login` to receive a token.
- **Caching:** The backend utilizes **Redis** for performance. Frontend developers should be aware that some list operations (like Campaigns) might be served from cache.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
