import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CampaignFormComponent } from './campaign-form.component';
import { CampaignService } from '../../core/services/campaign.service';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { of, BehaviorSubject, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { Campaign } from '../../core/models/campaign.model';
import { By } from '@angular/platform-browser';

describe('CampaignFormComponent', () => {
  let component: CampaignFormComponent;
  let fixture: ComponentFixture<CampaignFormComponent>;
  let mockCampaignService: jasmine.SpyObj<CampaignService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let paramsSubject: BehaviorSubject<any>;

  const dummyCampaign: Campaign = {
    id: 1, name: 'Test Campaign', description: 'Test Desc', 
    creationDate: '2023-01-01', status: 'ACTIVE', gameMasterId: 1, gameMasterName: 'GM'
  };

  beforeEach(async () => {
    mockCampaignService = jasmine.createSpyObj('CampaignService', ['getCampaign', 'createCampaign', 'updateCampaign']);
    mockCampaignService.getCampaign.and.returnValue(of(dummyCampaign));
    mockCampaignService.createCampaign.and.returnValue(of(dummyCampaign));
    mockCampaignService.updateCampaign.and.returnValue(of(dummyCampaign));

    paramsSubject = new BehaviorSubject({});

    await TestBed.configureTestingModule({
      imports: [CampaignFormComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: CampaignService, useValue: mockCampaignService },
        { provide: ActivatedRoute, useValue: { params: paramsSubject.asObservable() } }
      ]
    })
    .compileComponents();

    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    spyOn(mockRouter, 'navigate');

    fixture = TestBed.createComponent(CampaignFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize in Create Mode (default)', () => {
    expect(component.isEditMode).toBeFalse();
    expect(component.campaignForm.valid).toBeFalse(); // Name is required
  });

  it('should switch to Edit Mode and load data when id param provided', () => {
    paramsSubject.next({ id: '1' });
    fixture.detectChanges();

    expect(component.isEditMode).toBeTrue();
    expect(component.campaignId).toBe(1);
    expect(mockCampaignService.getCampaign).toHaveBeenCalledWith(1);
    expect(component.campaignForm.value).toEqual({
      name: dummyCampaign.name,
      description: dummyCampaign.description
    });
  });

  it('should call createCampaign on submit in Create Mode', () => {
    component.campaignForm.setValue({
      name: 'New Campaign',
      description: 'New Desc'
    });
    
    component.onSubmit();

    expect(mockCampaignService.createCampaign).toHaveBeenCalledWith(jasmine.objectContaining({
      name: 'New Campaign',
      description: 'New Desc',
      gameMasterId: 1
    }));
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  });

  it('should call updateCampaign on submit in Edit Mode', () => {
    // Switch to Edit Mode
    paramsSubject.next({ id: '1' });
    fixture.detectChanges();
    
    // Modify form
    component.campaignForm.patchValue({
      name: 'Updated Name'
    });

    component.onSubmit();

    expect(mockCampaignService.updateCampaign).toHaveBeenCalledWith(1, jasmine.objectContaining({
      name: 'Updated Name',
      description: 'Test Desc', // Original value retained if not changed in form
      gameMasterId: 1
    }));
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  });

  it('should not submit if form is invalid', () => {
    component.campaignForm.setValue({
      name: '', // Invalid
      description: 'Desc'
    });

    component.onSubmit();

    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
  });

  it('should log error when loading campaign fails', () => {
    mockCampaignService.getCampaign.and.returnValue(throwError(() => new Error('Load failed')));
    spyOn(console, 'error');
    
    paramsSubject.next({ id: '1' });
    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Error loading campaign', jasmine.any(Error));
  });

  it('should log error when creating campaign fails', () => {
    mockCampaignService.createCampaign.and.returnValue(throwError(() => new Error('Create failed')));
    spyOn(console, 'error');
    
    component.campaignForm.setValue({ name: 'New', description: 'Desc' });
    component.onSubmit();

    expect(console.error).toHaveBeenCalledWith('Error creating campaign', jasmine.any(Error));
  });

  it('should log error when updating campaign fails', () => {
    mockCampaignService.updateCampaign.and.returnValue(throwError(() => new Error('Update failed')));
    spyOn(console, 'error');
    
    paramsSubject.next({ id: '1' });
    fixture.detectChanges();
    
    component.onSubmit();

    expect(console.error).toHaveBeenCalledWith('Error updating campaign', jasmine.any(Error));
  });
});
