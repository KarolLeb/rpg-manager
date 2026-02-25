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

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.userRoles = user?.roles || [];
      if (this.userRoles.includes('GM')) {
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
