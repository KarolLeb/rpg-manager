import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUserValue;

  // 1. Sprawdź czy użytkownik jest zalogowany
  if (!authService.isLoggedIn()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // 2. Jeśli zalogowany, sprawdź czy trasa wymaga konkretnych ról (RBAC)
  const requiredRoles = route.data['roles'] as string[];
  if (requiredRoles && user && !requiredRoles.includes(user.role)) {
    // Użytkownik jest zalogowany, ale nie ma wymaganej roli
    console.warn(`Access denied for role: ${user.role}. Required: ${requiredRoles.join(',')}`);
    router.navigate(['/dashboard']);
    return false;
  }

  return true;
};
