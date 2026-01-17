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
    path: 'campaigns',
    loadComponent: () => import('./features/campaign/campaign-list.component')
      .then(m => m.CampaignListComponent)
  },
  {
    path: 'campaigns/new',
    loadComponent: () => import('./features/campaign/campaign-form.component')
      .then(m => m.CampaignFormComponent)
  },
  {
    path: 'campaigns/:id/edit',
    loadComponent: () => import('./features/campaign/campaign-form.component')
      .then(m => m.CampaignFormComponent)
  },
  {
    path: '**', // Wildcard (404)
    redirectTo: 'dashboard'
  }
];