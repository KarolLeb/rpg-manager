import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CampaignService } from '../../core/services/campaign.service';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { User } from '../../core/models/auth.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let mockAuthService: any;
  let userSubject: BehaviorSubject<User | null>;

  beforeEach(async () => {
    userSubject = new BehaviorSubject<User | null>(null);
    mockAuthService = {
      currentUser$: userSubject.asObservable(),
      isLoggedIn: () => true
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService }
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should show GM dashboard when user role is GM', () => {
    userSubject.next({ username: 'gm', role: 'GM' });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.gm-dashboard')).toBeTruthy();
  });

  it('should show Player dashboard when user role is PLAYER', () => {
    userSubject.next({ username: 'player', role: 'PLAYER' });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.player-dashboard')).toBeTruthy();
  });

  it('should handle error when loading campaigns', () => {
    const campaignService = TestBed.inject(CampaignService);
    spyOn(campaignService, 'getCampaigns').and.returnValue(throwError(() => new Error('Error')));
    
    userSubject.next({ username: 'gm', role: 'GM' });
    fixture.detectChanges();
    
    expect(component.error).toBe('Failed to load campaigns.');
    expect(component.isLoading).toBeFalse();
  });

  it('should not load campaigns for non-GM users', () => {
    userSubject.next({ username: 'player', role: 'PLAYER' });
    fixture.detectChanges();
    
    expect(component.isLoading).toBeFalse();
    expect(component.campaigns.length).toBe(0);
  });
});
