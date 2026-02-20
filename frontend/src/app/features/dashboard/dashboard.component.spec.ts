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
    // Before first detectChanges, isLoading should be true
    expect(component.isLoading).toBeTrue();
    
    userSubject.next(null);
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.isLoading).toBeFalse(); // BehaviorSubject emits immediately, not GM -> false
    expect(component.error).toBeNull();
  });

  it('should show GM dashboard when user role is GM and manage isLoading correctly', fakeAsync(() => {
    // Setup delayed response to catch isLoading = true
    mockCampaignService.getCampaigns.and.returnValue(of([]).pipe(delay(10)));

    userSubject.next({ id: 1, username: 'gm', role: 'GM' });
    fixture.detectChanges(); // calls ngOnInit -> subscribe -> loadCampaigns -> sets isLoading = true

    expect(component.isLoading).toBeTrue();
    expect(component.userRole).toBe('GM');
    expect(mockCampaignService.getCampaigns).toHaveBeenCalled();

    tick(10); // Finish loading -> sets isLoading = false
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.gm-dashboard')).toBeTruthy();
    expect(component.isLoading).toBeFalse();
  }));

  it('should show Player dashboard when user role is PLAYER and stop loading', () => {
    userSubject.next({ id: 2, username: 'player', role: 'PLAYER' });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.player-dashboard')).toBeTruthy();
    expect(component.userRole).toBe('PLAYER');
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.getCampaigns).not.toHaveBeenCalled();
  });

  it('should handle error when loading campaigns and stop loading', fakeAsync(() => {
    // Setup error response with delay
    mockCampaignService.getCampaigns.and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => new Error('Error')))));

    userSubject.next({ id: 1, username: 'gm', role: 'GM' });
    fixture.detectChanges();

    expect(component.isLoading).toBeTrue();

    tick(10); // Process error

    expect(component.error).toBe('Failed to load campaigns.');
    expect(component.isLoading).toBeFalse();
  }));

  it('should not load campaigns for non-GM users and stop loading', () => {
    userSubject.next({ id: 2, username: 'player', role: 'PLAYER' });
    fixture.detectChanges();

    expect(component.isLoading).toBeFalse();
    expect(component.campaigns.length).toBe(0);
  });

  it('should handle user without role correctly', () => {
    userSubject.next({ id: 3, username: 'norole' } as any);
    fixture.detectChanges();
    
    expect(component.userRole).toBeNull();
    expect(component.isLoading).toBeFalse();
    expect(mockCampaignService.getCampaigns).not.toHaveBeenCalled();
  });
});
