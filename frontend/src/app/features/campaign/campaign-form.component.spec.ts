import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CampaignFormComponent } from './campaign-form.component';
import { CampaignService } from '../../core/services/campaign.service';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { of, BehaviorSubject, throwError, delay, switchMap } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { Campaign } from '../../core/models/campaign.model';
import { AuthService } from '../../core/services/auth.service';

describe('CampaignFormComponent', () => {
  let component: CampaignFormComponent;
  let fixture: ComponentFixture<CampaignFormComponent>;
  let mockCampaignService: jasmine.SpyObj<CampaignService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let paramsSubject: BehaviorSubject<any>;

  const dummyCampaign: Campaign = {
    id: 1, name: 'Test Campaign', description: 'Test Desc', 
    creationDate: '2023-01-01', status: 'ACTIVE', gameMasterId: 1, gameMasterName: 'GM'
  };

  beforeEach(async () => {
    mockCampaignService = jasmine.createSpyObj('CampaignService', ['getCampaign', 'createCampaign', 'updateCampaign']);
    mockCampaignService.getCampaign.and.returnValue(of(dummyCampaign).pipe(delay(0)));
    mockCampaignService.createCampaign.and.returnValue(of(dummyCampaign).pipe(delay(0)));
    mockCampaignService.updateCampaign.and.returnValue(of(dummyCampaign).pipe(delay(0)));

    mockAuthService = jasmine.createSpyObj('AuthService', ['login', 'logout'], {
      currentUserValue: { username: 'test', role: 'GM' }
    });

    paramsSubject = new BehaviorSubject({});

    await TestBed.configureTestingModule({
      imports: [CampaignFormComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: CampaignService, useValue: mockCampaignService },
        { provide: AuthService, useValue: mockAuthService },
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

  it('should switch to Edit Mode and load data when id param provided', fakeAsync(() => {
    paramsSubject.next({ id: '1' });
    tick();
    fixture.detectChanges();

    expect(component.isEditMode).toBeTrue();
    expect(component.campaignId).toBe(1);
    expect(mockCampaignService.getCampaign).toHaveBeenCalledWith(1);
    expect(component.campaignForm.value).toEqual({
      name: dummyCampaign.name,
      description: dummyCampaign.description
    });
  }));

  it('should call createCampaign on submit in Create Mode and navigate on success', fakeAsync(() => {
    component.campaignForm.setValue({
      name: 'New Campaign',
      description: 'New Desc'
    });
    
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    
    expect(mockCampaignService.createCampaign).toHaveBeenCalledTimes(1);
    tick(); // Wait for async response
    expect(component.isLoading).toBeFalse();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  }));

  it('should call updateCampaign on submit in Edit Mode and navigate on success', fakeAsync(() => {
    paramsSubject.next({ id: '1' });
    tick();
    fixture.detectChanges();
    
    component.campaignForm.patchValue({ name: 'Updated Name', description: 'Updated Desc' });
    expect(component.isLoading).toBeFalse();
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    
    expect(mockCampaignService.updateCampaign).toHaveBeenCalledTimes(1);
    tick();
    expect(component.isLoading).toBeFalse();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  }));

  it('should set error if user is not logged in and NOT call service', fakeAsync(() => {
    (Object.getOwnPropertyDescriptor(mockAuthService, 'currentUserValue')?.get as jasmine.Spy).and.returnValue(null);
    
    component.campaignForm.setValue({ name: 'New', description: 'Desc' });
    component.onSubmit();
    
    expect(component.error).toBe('Musisz być zalogowany, aby stworzyć kampanię.');
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
  }));

  it('should handle creation error and set custom error message', fakeAsync(() => {
    const error = new Error('Create failed');
    mockCampaignService.createCampaign.and.returnValue(of(null).pipe(delay(0), switchMap(() => throwError(() => error))));
    spyOn(console, 'error');
    
    component.campaignForm.setValue({ name: 'New', description: 'Desc' });
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    tick();

    expect(console.error).toHaveBeenCalledWith('Error creating campaign', error);
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBe('Wystąpił błąd podczas tworzenia kampanii.');
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));

  it('should handle update error and set custom error message', fakeAsync(() => {
    paramsSubject.next({ id: '1' });
    tick();
    fixture.detectChanges();

    const error = new Error('Update failed');
    mockCampaignService.updateCampaign.and.returnValue(of(null).pipe(delay(0), switchMap(() => throwError(() => error))));
    spyOn(console, 'error');
    
    component.onSubmit();
    expect(component.isLoading).toBeTrue();
    tick();

    expect(console.error).toHaveBeenCalledWith('Error updating campaign', error);
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBe('Wystąpił błąd podczas aktualizacji kampanii.');
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));

  it('should return immediately if form is invalid', fakeAsync(() => {
    component.campaignForm.patchValue({ name: '' }); // Invalid
    component.onSubmit();
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
  }));

  it('should NOT update form if getCampaign fails', fakeAsync(() => {
    mockCampaignService.getCampaign.and.returnValue(throwError(() => new Error('fail')).pipe(delay(0)));
    spyOn(console, 'error');
    
    component.loadCampaign(123);
    tick();
    
    expect(console.error).toHaveBeenCalled();
    expect(component.campaignForm.value.name).toBe('');
  }));
});
