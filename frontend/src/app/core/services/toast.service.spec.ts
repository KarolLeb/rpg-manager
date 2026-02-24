import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add and remove toasts', () => {
    service.success('First message');
    service.success('Second message');
    
    expect(service.toasts().length).toBe(2);
    const firstId = service.toasts()[0].id;
    const secondId = service.toasts()[1].id;
    
    expect(secondId).toBe(firstId + 1);

    service.remove(firstId);
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].id).toBe(secondId);
    
    service.remove(secondId);
    expect(service.toasts().length).toBe(0);
  });

  it('should auto-remove toasts after duration', fakeAsync(() => {
    service.info('Info message', 1000);
    expect(service.toasts().length).toBe(1);
    
    tick(1001);
    expect(service.toasts().length).toBe(0);
  }));

  it('should NOT auto-remove toasts if duration is 0', fakeAsync(() => {
    service.show('Permanent', 'info', 0);
    expect(service.toasts().length).toBe(1);
    
    tick(10000);
    expect(service.toasts().length).toBe(1);
  }));

  it('should use default type and duration when not provided to show', fakeAsync(() => {
    service.show('Default');
    expect(service.toasts()[0].type).toBe('info');
    expect(service.toasts()[0].duration).toBe(3000);
    
    tick(2999);
    expect(service.toasts().length).toBe(1);
    tick(2);
    expect(service.toasts().length).toBe(0);
  }));

  it('should support different toast types', () => {
    service.error('Error');
    service.warning('Warning');
    service.info('Info');
    
    expect(service.toasts().length).toBe(3);
    expect(service.toasts().find(t => t.type === 'error')).toBeTruthy();
    expect(service.toasts().find(t => t.type === 'warning')).toBeTruthy();
    expect(service.toasts().find(t => t.type === 'info')).toBeTruthy();
  });
});
