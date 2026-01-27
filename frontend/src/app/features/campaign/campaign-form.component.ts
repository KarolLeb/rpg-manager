import { Component, OnInit, inject } from '@angular/core';

import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CampaignService } from '../../core/services/campaign.service';
import { AuthService } from '../../core/services/auth.service';
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
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  campaignForm: FormGroup;
  isEditMode = false;
  campaignId: number | null = null;
  error: string | null = null;

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
    this.campaignService.getCampaign(id).subscribe({
      next: (campaign: Campaign) => {
        this.campaignForm.patchValue({
          name: campaign.name,
          description: campaign.description
        });
      },
      error: (err: any) => console.error('Error loading campaign', err)
    });
  }

  onSubmit(): void {
    if (this.campaignForm.invalid) {
      return;
    }

    const user = this.authService.currentUserValue;
    if (!user) {
      this.error = 'Musisz być zalogowany, aby stworzyć kampanię.';
      return;
    }

    const request: CreateCampaignRequest = {
      name: this.campaignForm.value.name,
      description: this.campaignForm.value.description,
      gameMasterId: 1
    };

    if (this.isEditMode && this.campaignId) {
      this.campaignService.updateCampaign(this.campaignId, request).subscribe({
        next: () => {
          this.router.navigate(['/campaigns']);
        },
        error: (err: any) => {
          this.error = 'Wystąpił błąd podczas aktualizacji kampanii.';
          console.error('Error updating campaign', err);
        }
      });
    } else {
      this.campaignService.createCampaign(request).subscribe({
        next: () => {
          this.router.navigate(['/campaigns']);
        },
        error: (err: any) => {
          this.error = 'Wystąpił błąd podczas tworzenia kampanii.';
          console.error('Error creating campaign', err);
        }
      });
    }
  }
}