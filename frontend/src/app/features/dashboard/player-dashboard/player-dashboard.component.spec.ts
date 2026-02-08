import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { PlayerDashboardComponent } from './player-dashboard.component';
import { provideRouter } from '@angular/router';
import { CharacterService } from '../../../core/services/character.service';
import { of, throwError, delay, switchMap } from 'rxjs';

describe('PlayerDashboardComponent', () => {
  let component: PlayerDashboardComponent;
  let fixture: ComponentFixture<PlayerDashboardComponent>;
  let mockCharacterService: jasmine.SpyObj<CharacterService>;

  beforeEach(async () => {
    mockCharacterService = jasmine.createSpyObj('CharacterService', ['getCharacters']);
    mockCharacterService.getCharacters.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [PlayerDashboardComponent],
      providers: [
        provideRouter([]),
        { provide: CharacterService, useValue: mockCharacterService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlayerDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBeNull();
  });

  it('should load characters on init and manage state correctly', fakeAsync(() => {
    const mockCharacters = [{ id: 1, name: 'Char 1', characterClass: 'Warrior', level: 1, stats: '', ownerId: 1, controllerId: 1, campaignName: 'C1', campaignId: 1, characterType: 'PERMANENT' }];
    mockCharacterService.getCharacters.and.returnValue(of(mockCharacters).pipe(delay(10)));

    expect(component.isLoading).toBeTrue();
    expect(component.characters.length).toBe(0);

    fixture.detectChanges(); // calls ngOnInit

    expect(component.isLoading).toBeTrue();

    tick(10);
    fixture.detectChanges();

    expect(component.characters.length).toBe(1);
    expect(component.characters[0].name).toBe('Char 1');
    expect(component.isLoading).toBeFalse();
    expect(component.error).toBeNull();
    
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.character-card')).toBeTruthy();
    expect(compiled.querySelector('.no-data')).toBeFalsy();
  }));

  it('should handle zero characters correctly', fakeAsync(() => {
    mockCharacterService.getCharacters.and.returnValue(of([]).pipe(delay(10)));

    fixture.detectChanges();
    tick(10);
    fixture.detectChanges();

    expect(component.characters.length).toBe(0);
    expect(component.isLoading).toBeFalse();
    
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.no-data')).toBeTruthy();
    expect(compiled.querySelector('.character-card')).toBeFalsy();
  }));

  it('should set error on load failure and stop loading', fakeAsync(() => {
    mockCharacterService.getCharacters.and.returnValue(of(null).pipe(delay(10), switchMap(() => throwError(() => new Error('Error')))));

    expect(component.isLoading).toBeTrue();
    fixture.detectChanges();
    expect(component.isLoading).toBeTrue();

    tick(10);
    fixture.detectChanges();

    expect(component.error).toBe('Failed to load characters.');
    expect(component.isLoading).toBeFalse();
    expect(component.characters.length).toBe(0);
    
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.error')).toBeTruthy();
    expect(compiled.querySelector('.loading')).toBeFalsy();
  }));
});
