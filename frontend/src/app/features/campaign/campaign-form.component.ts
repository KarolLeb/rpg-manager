import { Component, OnInit, inject } from '@angular/core';

import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CampaignService } from '../../core/services/campaign.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { CreateCampaignRequest, Campaign } from '../../core/models/campaign.model';

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './campaign-form.component.html',
  styles: [`
    .form-container { padding: 20px; max-width: 600px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; font-weight: bold; }
    input, textarea { width: 100%; padding: 8px; box-sizing: border-box; }
    textarea { height: 100px; }
    button { padding: 10px 20px; background-color: #28a745; color: white; border: none; cursor: pointer; border-radius: 5px; margin-right: 10px; }
    button:disabled { background-color: #ccc; }
    .cancel-btn { background-color: #6c757d; text-decoration: none; display: inline-block; }
  `]
})
export class CampaignFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly campaignService = inject(CampaignService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  campaignForm: FormGroup;
  isEditMode = false;
  campaignId: number | null = null;
  error: string | null = null;
  isLoading = false;

  constructor() {
    this.campaignForm = this.fb.group({
      name: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.campaignId = +params['id'];
        this.loadCampaign(this.campaignId);
      }
    });
  }

  loadCampaign(id: number): void {
    this.isLoading = true;
    this.campaignService.getCampaign(id).subscribe({
      next: (campaign: Campaign) => {
        this.isLoading = false;
        if (campaign) {
          this.campaignForm.patchValue({
            name: campaign.name,
            description: campaign.description
          });
        }
      },
      error: (err: any) => {
        this.isLoading = false;
        console.error('Error loading campaign', err);
        this.toastService.error('Error loading campaign details');
      }
    });
  }

  onSubmit(): void {
    if (this.campaignForm.invalid) {
      return;
    }

    this.error = null;
    const user = this.authService.currentUserValue;
    if (!user) {
      this.toastService.error('You must be logged in to create a campaign.');
      return;
    }

    this.isLoading = true;
    const request: CreateCampaignRequest = {
      name: this.campaignForm.value.name,
      description: this.campaignForm.value.description,
      gameMasterId: user.id
    };

    if (this.isEditMode && this.campaignId) {
      this.campaignService.updateCampaign(this.campaignId, request).subscribe({
        next: () => {
          this.isLoading = false;
          this.toastService.success('Campaign updated successfully!');
          this.router.navigate(['/campaigns']);
        },
        error: (err: any) => {
          this.isLoading = false;
          this.error = 'Wystąpił błąd podczas aktualizacji kampanii.';
          this.toastService.error(this.error);
          console.error('Error updating campaign', err);
        }
      });
    } else {
      this.campaignService.createCampaign(request).subscribe({
        next: () => {
          this.isLoading = false;
          this.toastService.success('Campaign created successfully!');
          this.router.navigate(['/campaigns']);
        },
        error: (err: any) => {
          this.isLoading = false;
          this.error = 'Wystąpił błąd podczas tworzenia kampanii.';
          this.toastService.error(this.error);
          console.error('Error creating campaign', err);
        }
      });
    }
  }
}