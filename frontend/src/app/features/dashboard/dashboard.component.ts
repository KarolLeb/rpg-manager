import { Component, OnInit, inject } from '@angular/core';

import { AuthService } from '../../core/services/auth.service';
import { CampaignService } from '../../core/services/campaign.service';
import { Campaign } from '../../core/models/campaign.model';
import { RouterLink } from '@angular/router';
import { PlayerDashboardComponent } from './player-dashboard/player-dashboard.component';
import { AdminDashboardComponent } from '../admin/admin-dashboard.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, PlayerDashboardComponent, AdminDashboardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly campaignService = inject(CampaignService);

  userRoles: string[] = [];
  campaigns: Campaign[] = [];
  isLoading = true;
  error: string | null = null;
  currentUser: any = null;

  get canCreateCampaign(): boolean {
    return this.currentUser && (this.userRoles.includes('GM') || this.userRoles.includes('ADMIN'));
  }

  canEditCampaign(campaign: Campaign): boolean {
    if (!this.currentUser) return false;
    return this.userRoles.includes('ADMIN') || campaign.gameMasterId === this.currentUser.id;
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.userRoles = user?.roles || [];
      if (user) {
        this.loadCampaigns();
      } else {
        this.isLoading = false;
      }
    });
  }

  loadCampaigns(): void {
    this.isLoading = true;
    this.campaignService.getCampaigns().subscribe({
      next: (data: Campaign[]) => {
        this.campaigns = data;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.error = 'Failed to load campaigns.';
        this.isLoading = false;
      }
    });
  }
}
