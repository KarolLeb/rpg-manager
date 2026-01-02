import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
  },
  {
    path: 'character', 
    loadComponent: () => import('./features/character-sheet/character-sheet.component')
      .then(m => m.CharacterSheetPageComponent)
  },
  {
    path: '**', // Wildcard (404)
    redirectTo: 'dashboard'
  }
];