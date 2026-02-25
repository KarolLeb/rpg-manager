import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
  },
  {
    path: 'character/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/character-sheet/character-sheet.component')
      .then(m => m.CharacterSheetPageComponent)
  },
  {
    path: 'campaigns',
    canActivate: [authGuard],
    loadComponent: () => import('./features/campaign/campaign-list.component')
      .then(m => m.CampaignListComponent)
  },
  {
    path: 'campaigns/new',
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'GM'] },
    loadComponent: () => import('./features/campaign/campaign-form.component')
      .then(m => m.CampaignFormComponent)
  },
  {
    path: 'campaigns/:id/edit',
    canActivate: [authGuard],
    loadComponent: () => import('./features/campaign/campaign-form.component')
      .then(m => m.CampaignFormComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () => import('./features/admin/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },
  {
    path: '**', // Wildcard (404)
    redirectTo: 'dashboard'
  }
];