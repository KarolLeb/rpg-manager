import { Component, OnInit, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';

import { RouterModule } from '@angular/router';
import { CampaignService } from '../../core/services/campaign.service';
import { Campaign } from '../../core/models/campaign.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [RouterModule, AsyncPipe],
  templateUrl: './campaign-list.component.html',
  styles: [`
    .campaign-container { padding: 20px; }
    .campaign-card { border: 1px solid #ccc; padding: 15px; margin-bottom: 10px; border-radius: 5px; }
    .actions { margin-top: 10px; }
    button { margin-right: 10px; cursor: pointer; }
    .create-btn { margin-bottom: 20px; display: inline-block; padding: 10px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    .delete-btn { background-color: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; }
    .edit-btn { background-color: #ffc107; color: black; padding: 5px 10px; text-decoration: none; border-radius: 3px; }
  `]
})
export class CampaignListComponent implements OnInit {
  private readonly campaignService = inject(CampaignService);
  private readonly authService = inject(AuthService);

  campaigns: Campaign[] = [];
  currentUser$ = this.authService.currentUser$;

  ngOnInit(): void {
    this.loadCampaigns();
  }

  loadCampaigns(): void {
    this.campaignService.getCampaigns().subscribe({
      next: (data: Campaign[]) => this.campaigns = data,
      error: (err: any) => console.error('Error loading campaigns', err)
    });
  }

  deleteCampaign(id: number): void {
    if (confirm('Are you sure you want to delete this campaign?')) {
      this.campaignService.deleteCampaign(id).subscribe({
        next: () => this.loadCampaigns(),
        error: (err: any) => console.error('Error deleting campaign', err)
      });
    }
  }

  canEdit(campaign: Campaign, user: any): boolean {
    if (!user) return false;
    return user.roles?.includes('ADMIN') || campaign.gameMasterId === user.id;
  }
}