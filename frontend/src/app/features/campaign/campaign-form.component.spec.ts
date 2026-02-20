import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CampaignFormComponent } from './campaign-form.component';
import { CampaignService } from '../../core/services/campaign.service';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { of, BehaviorSubject, throwError, delay, switchMap } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { Campaign } from '../../core/models/campaign.model';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

describe('CampaignFormComponent', () => {
  let component: CampaignFormComponent;
  let fixture: ComponentFixture<CampaignFormComponent>;
  let mockCampaignService: jasmine.SpyObj<CampaignService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockToastService: jasmine.SpyObj<ToastService>;
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

    mockAuthService = jasmine.createSpyObj('AuthService', ['login', 'logout']);
    mockToastService = jasmine.createSpyObj('ToastService', ['success', 'error', 'warning']);
    // Setup getter for currentUserValue
    Object.defineProperty(mockAuthService, 'currentUserValue', {
      get: () => ({ id: 1, username: 'test', role: 'GM' }),
      configurable: true
    });

    paramsSubject = new BehaviorSubject({});

    await TestBed.configureTestingModule({
      imports: [CampaignFormComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: CampaignService, useValue: mockCampaignService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: ToastService, useValue: mockToastService },
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
    expect(component.isLoading).toBe(false);
    expect(component.isEditMode).toBe(false);
    expect(component.campaignId).toBeNull();
    expect(component.error).toBeNull();
    // Verify default form values
    expect(component.campaignForm.value).toEqual({ name: '', description: '' });
    expect(component.campaignForm.valid).toBe(false); // name is required

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Create New Campaign');
    expect(compiled.querySelector('button[type="submit"]')?.textContent).toContain('Save');
    
    const cancelBtn = compiled.querySelector('.cancel-btn');
    expect(cancelBtn?.getAttribute('routerLink')).toBe('/campaigns');
  });

  it('should have required validator on name control and check exact default value', () => {
    const nameControl = component.campaignForm.get('name');
    expect(nameControl?.value).toBe(''); // Check StringLiteral mutation
    
    nameControl?.setValue('');
    nameControl?.markAsTouched();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(nameControl?.valid).toBeFalse();
    expect(nameControl?.errors?.['required']).toBeTruthy(); // Check ArrayDeclaration/Validators mutation
    expect(compiled.querySelector('.form-group div')?.textContent).toContain('Name is required.');
    
    nameControl?.setValue('Some Name');
    fixture.detectChanges();
    expect(nameControl?.valid).toBeTrue();
    expect(compiled.querySelector('.form-group div')).toBeNull();
  });

  it('should switch to Edit Mode and load data when id param provided', fakeAsync(() => {
    // Test initial state before param
    expect(component.isEditMode).toBe(false);

    // Setup mock to be async
    mockCampaignService.getCampaign.and.returnValue(of(dummyCampaign).pipe(delay(1)));

    paramsSubject.next({ id: '1' });
    // detectChanges triggered by params subscription -> calls loadCampaign
    fixture.detectChanges();

    expect(component.isEditMode).toBe(true);
    expect(component.campaignId).toBe(1);
    expect(component.isLoading).toBe(true);

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Edit Campaign');
    expect(compiled.querySelector('button[type="submit"]')?.textContent).toContain('Saving...');

    tick(1);
    fixture.detectChanges();

    expect(component.isLoading).toBe(false);
    expect(mockCampaignService.getCampaign).toHaveBeenCalledWith(1);
    expect(compiled.querySelector('button[type="submit"]')?.textContent).toContain('Save');

    // Verify patchValue exactly
    expect(component.campaignForm.value).toEqual({
      name: dummyCampaign.name,
      description: dummyCampaign.description
    });

    // Verify it doesn't navigate on load
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));

  it('should handle invalid id param gracefully', fakeAsync(() => {
    paramsSubject.next({ id: 'abc' });
    tick();
    fixture.detectChanges();

    expect(component.isEditMode).toBe(true);
    expect(component.campaignId).toBeNaN();
    expect(mockCampaignService.getCampaign).toHaveBeenCalledWith(NaN);
  }));

  it('should treat id "0" as falsy and fall back to createCampaign in edit mode', fakeAsync(() => {
    paramsSubject.next({ id: '0' });
    tick();
    fixture.detectChanges();

    expect(component.isEditMode).toBe(true);
    expect(component.campaignId).toBe(0);

    component.campaignForm.setValue({ name: 'Zero', description: 'Zero' });
    component.onSubmit();

    expect(mockCampaignService.createCampaign).toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
    tick();
  }));

  it('should NOT load campaign if id is missing', fakeAsync(() => {
    paramsSubject.next({});
    tick();
    fixture.detectChanges();

    expect(component.isEditMode).toBeFalse();
    expect(mockCampaignService.getCampaign).not.toHaveBeenCalled();
  }));

  it('should call createCampaign on submit in Create Mode and navigate on success', fakeAsync(() => {
    component.campaignForm.setValue({
      name: 'New Campaign',
      description: 'New Desc'
    });

    mockCampaignService.createCampaign.and.returnValue(of(dummyCampaign).pipe(delay(1)));

    expect(component.isLoading).toBe(false);
    component.onSubmit();
    expect(component.isLoading).toBe(true);

    expect(mockCampaignService.createCampaign).toHaveBeenCalledWith({
      name: 'New Campaign',
      description: 'New Desc',
      gameMasterId: 1
    });
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();

    tick(1); // Wait for async response
    expect(component.isLoading).toBe(false);
    expect(mockToastService.success).toHaveBeenCalledWith('Campaign created successfully!');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
    // Check mutation ['/campaigns'] -> [] or [""]
    const navigateArgs = (mockRouter.navigate as jasmine.Spy).calls.argsFor(0)[0];
    expect(navigateArgs).toEqual(['/campaigns']);
    expect(navigateArgs[0]).toBe('/campaigns');
    expect(navigateArgs.length).toBe(1);
  }));

  it('should call updateCampaign on submit in Edit Mode and navigate on success', fakeAsync(() => {
    paramsSubject.next({ id: '1' });
    tick(); // this tick is for paramsSubject.next
    fixture.detectChanges();

    component.campaignForm.patchValue({ name: 'Updated Name', description: 'Updated Desc' });
    mockCampaignService.updateCampaign.and.returnValue(of(dummyCampaign).pipe(delay(1)));

    expect(component.isLoading).toBe(false);
    component.onSubmit();
    expect(component.isLoading).toBe(true);

    expect(mockCampaignService.updateCampaign).toHaveBeenCalledWith(1, {
      name: 'Updated Name',
      description: 'Updated Desc',
      gameMasterId: 1
    });
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();

    tick(1);
    expect(component.isLoading).toBe(false);
    expect(mockToastService.success).toHaveBeenCalledWith('Campaign updated successfully!');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
    expect((mockRouter.navigate as jasmine.Spy).calls.argsFor(0)[0]).toEqual(['/campaigns']);
  }));

  it('should NOT call updateCampaign if campaignId is missing even in edit mode', fakeAsync(() => {
    component.isEditMode = true;
    component.campaignId = null;

    component.campaignForm.setValue({ name: 'Test', description: 'Test' });
    component.onSubmit();

    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.createCampaign).toHaveBeenCalledWith(jasmine.objectContaining({ name: 'Test' }));
    tick();
    expect(component.isLoading).toBeFalse();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  }));

  it('should call createCampaign if isEditMode is false even if campaignId is set', fakeAsync(() => {
    component.isEditMode = false;
    component.campaignId = 1;

    component.campaignForm.setValue({ name: 'New', description: 'Desc' });
    component.onSubmit();

    expect(mockCampaignService.createCampaign).toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
    tick();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/campaigns']);
  }));

  it('should set toast error if user is not logged in and NOT call service', fakeAsync(() => {
    Object.defineProperty(mockAuthService, 'currentUserValue', {
      get: () => null,
      configurable: true
    });

    component.campaignForm.setValue({ name: 'New', description: 'Desc' });
    component.onSubmit();

    expect(mockToastService.error).toHaveBeenCalledWith('You must be logged in to create a campaign.');
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
  }));

    it('should handle creation error and set custom toast error message', fakeAsync(() => {

      const error = new Error('Create failed');

      mockCampaignService.createCampaign.and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => error))));

      spyOn(console, 'error');



      component.campaignForm.setValue({ name: 'New', description: 'Desc' });

      component.onSubmit();

      // isLoading is set to true immediately before subscription

      expect(component.isLoading).toBeTrue();

      tick(1);



      expect(console.error).toHaveBeenCalledWith('Error creating campaign', error);


    // Check mutation console.error("", err)
    expect((console.error as jasmine.Spy).calls.argsFor(0)[0]).not.toBe('');

    expect(component.isLoading).toBeFalse();
    // Check mutation isLoading = true in error block
    expect(component.isLoading).not.toBeTrue();

    expect(mockToastService.error).toHaveBeenCalledWith('Wystąpił błąd podczas tworzenia kampanii.');
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));

    it('should handle update error and set custom toast error message', fakeAsync(() => {

      paramsSubject.next({ id: '1' });

      tick();

      fixture.detectChanges();



      const error = new Error('Update failed');

      mockCampaignService.updateCampaign.and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => error))));

      spyOn(console, 'error');



      component.onSubmit();

      expect(component.isLoading).toBeTrue();

      tick(1);



      expect(console.error).toHaveBeenCalledWith('Error updating campaign', error);


    expect((console.error as jasmine.Spy).calls.argsFor(0)[0]).not.toBe('');

    expect(component.isLoading).toBeFalse();
    expect(component.isLoading).not.toBeTrue();

    expect(mockToastService.error).toHaveBeenCalledWith('Wystąpił błąd podczas aktualizacji kampanii.');
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  }));

  it('should reset error at the start of onSubmit', fakeAsync(() => {
    component.error = 'Old Error';
    component.campaignForm.setValue({ name: 'Valid', description: 'Valid' });
    
    mockCampaignService.createCampaign.and.returnValue(of(dummyCampaign).pipe(delay(1)));
    
    component.onSubmit();
    expect(component.error).toBeNull();
    tick(1);
    expect(component.error).toBeNull(); // Should still be null after success
  }));

  it('should return immediately if form is invalid', fakeAsync(() => {
    component.campaignForm.patchValue({ name: '' }); // Invalid
    component.onSubmit();
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.createCampaign).not.toHaveBeenCalled();
    expect(mockCampaignService.updateCampaign).not.toHaveBeenCalled();
  }));

  it('should NOT update form if getCampaign returns null', fakeAsync(() => {
    mockCampaignService.getCampaign.and.returnValue(of(null as any).pipe(delay(1)));
    
    component.loadCampaign(123);
    expect(component.isLoading).toBe(true);
    tick(1);

    expect(component.isLoading).toBe(false);
    expect(component.campaignForm.value.name).toBe('');
  }));

  it('should NOT update form if getCampaign fails and show toast error', fakeAsync(() => {
    mockCampaignService.getCampaign.and.returnValue(of(null).pipe(delay(1), switchMap(() => throwError(() => new Error('fail')))));
    spyOn(console, 'error');

    component.loadCampaign(123);
    expect(component.isLoading).toBe(true);
    tick(1);

    expect(component.isLoading).toBe(false);
    expect(console.error).toHaveBeenCalledWith('Error loading campaign', jasmine.any(Error));
    expect(mockToastService.error).toHaveBeenCalledWith('Error loading campaign details');
    expect(component.campaignForm.value.name).toBe('');
  }));
});
