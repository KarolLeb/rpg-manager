import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastComponent } from './toast.component';
import { ToastService } from '../../../core/services/toast.service';

describe('ToastComponent', () => {
  let component: ToastComponent;
  let fixture: ComponentFixture<ToastComponent>;
  let toastService: ToastService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastComponent],
      providers: [ToastService]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    toastService = TestBed.inject(ToastService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display toasts and allow removal', () => {
    toastService.success('Test Success');
    fixture.detectChanges();

    const toastItems = fixture.nativeElement.querySelectorAll('.toast-item');
    expect(toastItems.length).toBe(1);
    expect(toastItems[0].textContent).toContain('Test Success');

    // Click to remove
    const closeBtn = fixture.nativeElement.querySelector('.toast-close');
    closeBtn.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('.toast-item').length).toBe(0);
  });

  it('should call toastService.remove on remove()', () => {
    spyOn(toastService, 'remove');
    component.remove(123);
    expect(toastService.remove).toHaveBeenCalledWith(123);
  });
});
