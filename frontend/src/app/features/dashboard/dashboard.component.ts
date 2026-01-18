import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { CampaignService } from '../../core/services/campaign.service';
import { Campaign } from '../../core/models/campaign.model';
import { RouterLink } from '@angular/router';
import { PlayerDashboardComponent } from './player-dashboard/player-dashboard.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, PlayerDashboardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  userRole: string | null = null;
  campaigns: Campaign[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private campaignService: CampaignService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.userRole = user?.role || null;
      if (this.userRole === 'GM') {
        this.loadCampaigns();
      } else {
        this.isLoading = false;
      }
    });
  }

  loadCampaigns(): void {
    this.isLoading = true;
    this.campaignService.getCampaigns().subscribe({
      next: (data) => {
        this.campaigns = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load campaigns.';
        this.isLoading = false;
      }
    });
  }
}
