import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CampaignListComponent } from './campaign-list.component';
import { CampaignService } from '../../core/services/campaign.service';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { Campaign } from '../../core/models/campaign.model';
import { By } from '@angular/platform-browser';

describe('CampaignListComponent', () => {
  let component: CampaignListComponent;
  let fixture: ComponentFixture<CampaignListComponent>;
  let mockCampaignService: jasmine.SpyObj<CampaignService>;

  const dummyCampaigns: Campaign[] = [
    { id: 1, name: 'Campaign 1', description: 'Desc 1', creationDate: '2023-01-01', status: 'ACTIVE', gameMasterId: 10, gameMasterName: 'GM1' },
    { id: 2, name: 'Campaign 2', description: 'Desc 2', creationDate: '2023-01-02', status: 'FINISHED', gameMasterId: 10, gameMasterName: 'GM1' }
  ];

  beforeEach(async () => {
    mockCampaignService = jasmine.createSpyObj('CampaignService', ['getCampaigns', 'deleteCampaign']);
    mockCampaignService.getCampaigns.and.returnValue(of(dummyCampaigns));
    mockCampaignService.deleteCampaign.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [CampaignListComponent],
      providers: [
        provideRouter([]),
        { provide: CampaignService, useValue: mockCampaignService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CampaignListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load campaigns on init', () => {
    expect(component.campaigns.length).toBe(2);
    expect(component.campaigns).toEqual(dummyCampaigns);
    expect(mockCampaignService.getCampaigns).toHaveBeenCalled();
  });

  it('should render campaign cards', () => {
    const campaignCards = fixture.debugElement.queryAll(By.css('.campaign-card'));
    expect(campaignCards.length).toBe(2);
    expect(campaignCards[0].nativeElement.textContent).toContain('Campaign 1');
    expect(campaignCards[1].nativeElement.textContent).toContain('Campaign 2');
  });

  it('should call deleteCampaign when delete button is clicked and confirmed', () => {
    spyOn(globalThis, 'confirm').and.returnValue(true);
    const deleteButtons = fixture.debugElement.queryAll(By.css('.delete-btn'));

    // Check if buttons exist. If using *ngFor, we expect 2.
    expect(deleteButtons.length).toBeGreaterThan(0);

    deleteButtons[0].nativeElement.click();

    expect(globalThis.confirm).toHaveBeenCalledWith('Are you sure you want to delete this campaign?');
    expect(mockCampaignService.deleteCampaign).toHaveBeenCalledWith(1);
    expect(mockCampaignService.getCampaigns).toHaveBeenCalledTimes(2); // Once on init, once after delete
  });

  it('should NOT call deleteCampaign when delete button is clicked and NOT confirmed', () => {
    spyOn(globalThis, 'confirm').and.returnValue(false);
    const deleteButtons = fixture.debugElement.queryAll(By.css('.delete-btn'));

    deleteButtons[0].nativeElement.click();

    expect(globalThis.confirm).toHaveBeenCalledWith('Are you sure you want to delete this campaign?');
    expect(mockCampaignService.deleteCampaign).not.toHaveBeenCalled();
  });

  it('should log error when loading campaigns fails', () => {
    mockCampaignService.getCampaigns.and.returnValue(throwError(() => new Error('Load failed')));
    spyOn(console, 'error');

    component.loadCampaigns();

    expect(console.error).toHaveBeenCalledWith('Error loading campaigns', jasmine.any(Error));
  });

  it('should log error when deleting campaign fails', () => {
    spyOn(globalThis, 'confirm').and.returnValue(true);
    mockCampaignService.deleteCampaign.and.returnValue(throwError(() => new Error('Delete failed')));
    spyOn(console, 'error');

    component.deleteCampaign(1);

    expect(console.error).toHaveBeenCalledWith('Error deleting campaign', jasmine.any(Error));
  });
});
