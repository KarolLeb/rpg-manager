import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CampaignService } from '../../core/services/campaign.service';
import { BehaviorSubject, of, throwError, delay, switchMap } from 'rxjs';
import { User } from '../../core/models/auth.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockAuthService: any;
  let mockCampaignService: any;
  let userSubject: BehaviorSubject<User | null>;

  beforeEach(async () => {
    userSubject = new BehaviorSubject<User | null>(null);
    mockAuthService = {
      currentUser$: userSubject.asObservable(),
      isLoggedIn: () => true
    };
    mockCampaignService = {
      getCampaigns: jasmine.createSpy('getCampaigns').and.returnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService },
        { provide: CampaignService, useValue: mockCampaignService }
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBeNull();
  });

  it('should show GM dashboard when user role is GM', fakeAsync(() => {
    mockCampaignService.getCampaigns.and.returnValue(of([]).pipe(delay(10)));
    
    // Initial state check
    expect(component.isLoading).toBeTrue(); 

    userSubject.next({ id: 1, username: 'gm', role: 'GM' });
    fixture.detectChanges();
    
    // Should be loading now because of delay
    expect(component.isLoading).toBeTrue();
    
    tick(10); // Finish loading
    fixture.detectChanges();
    
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.gm-dashboard')).toBeTruthy();
    expect(component.userRole).toBe('GM');
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.getCampaigns).toHaveBeenCalled();
  }));

  it('should show Player dashboard when user role is PLAYER', () => {
    userSubject.next({ id: 2, username: 'player', role: 'PLAYER' });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.player-dashboard')).toBeTruthy();
    expect(component.userRole).toBe('PLAYER');
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.getCampaigns).not.toHaveBeenCalled();
  });

  it('should handle error when loading campaigns', fakeAsync(() => {
    mockCampaignService.getCampaigns.and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => new Error('Error')))));
    
    userSubject.next({ id: 1, username: 'gm', role: 'GM' });
    fixture.detectChanges();

    expect(component.isLoading).toBeTrue();
    
    tick(10);
    
    expect(component.error).toBe('Failed to load campaigns.');
    expect(component.isLoading).toBeFalse();
  }));

  it('should not load campaigns for non-GM users', () => {
    userSubject.next({ id: 2, username: 'player', role: 'PLAYER' });
    fixture.detectChanges();
    
    expect(component.isLoading).toBeFalse();
    expect(component.campaigns.length).toBe(0);
  });
});
