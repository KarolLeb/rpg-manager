import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('AdminDashboardComponent', () => {
  let component: AdminDashboardComponent;
  let fixture: ComponentFixture<AdminDashboardComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDashboardComponent, HttpClientTestingModule],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDashboardComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    // Initial expectation
    expect(component.isLoadingUsers).toBeTrue();
    
    fixture.detectChanges(); // calls ngOnInit
    
    const req = httpMock.expectOne('/api/admin/users');
    expect(req.request.method).toBe('GET');
    req.flush([]); // Flush empty to satisfy initial call

    expect(component).toBeTruthy();
  });

  it('should load users from API successfully', () => {
    const mockUsers = [
      { username: 'user1', role: 'ADMIN' },
      { username: 'user2', role: 'PLAYER' }
    ];

    fixture.detectChanges(); // ngOnInit -> loadUsers

    const req = httpMock.expectOne('/api/admin/users');
    req.flush(mockUsers);

    expect(component.users).toEqual(mockUsers);
    expect(component.usersCount).toBe(2);
    expect(component.isLoadingUsers).toBeFalse();
  });

  it('should handle API error and load fallback dummy data', () => {
    fixture.detectChanges(); // ngOnInit -> loadUsers

    const req = httpMock.expectOne('/api/admin/users');
    req.error(new ProgressEvent('Network error'));

    expect(component.users.length).toBe(2);
    expect(component.users[0].username).toBe('admin');
    expect(component.users[1].username).toBe('testuser');
    expect(component.usersCount).toBe(2);
    expect(component.isLoadingUsers).toBeFalse();
  });
});
