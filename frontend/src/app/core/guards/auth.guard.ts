import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastService = inject(ToastService);
  const user = authService.currentUserValue;

  // 1. Sprawdź czy użytkownik jest zalogowany
  if (!authService.isLoggedIn()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // 2. Jeśli zalogowany, sprawdź czy trasa wymaga konkretnych ról (RBAC)
  const requiredRoles = route.data['roles'] as string[];
  if (requiredRoles && user && !user.roles.some((r: string) => requiredRoles.includes(r))) {
    // Użytkownik jest zalogowany, ale nie ma wymaganej roli
    console.warn(`Access denied for roles: ${user.roles.join(', ')}. Required: ${requiredRoles.join(',')}`);
    toastService.error(`Access Denied. Required roles: ${requiredRoles.join(', ')}`);
    router.navigate(['/dashboard']);
    return false;
  }

  return true;
};
