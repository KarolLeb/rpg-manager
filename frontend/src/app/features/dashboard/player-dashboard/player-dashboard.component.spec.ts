import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlayerDashboardComponent } from './player-dashboard.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { CharacterService } from '../../../core/services/character.service';
import { of, throwError } from 'rxjs';

describe('PlayerDashboardComponent', () => {
  let component: PlayerDashboardComponent;
  let fixture: ComponentFixture<PlayerDashboardComponent>;
  let characterService: CharacterService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerDashboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        CharacterService
      ]
    })
    .compileComponents();
    
    characterService = TestBed.inject(CharacterService);
    fixture = TestBed.createComponent(PlayerDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load characters on init', () => {
    const mockCharacters = [{ id: 1, name: 'Char 1', characterClass: 'Warrior', level: 1, stats: '', ownerId: 1, controllerId: 1, campaignName: 'C1', campaignId: 1, characterType: 'PERMANENT' }];
    spyOn(characterService, 'getCharacters').and.returnValue(of(mockCharacters));

    fixture.detectChanges();

    expect(component.characters.length).toBe(1);
    expect(component.isLoading).toBeFalse();
  });

  it('should set error on load failure', () => {
    spyOn(characterService, 'getCharacters').and.returnValue(throwError(() => new Error('Error')));

    fixture.detectChanges();

    expect(component.error).toBe('Failed to load characters.');
    expect(component.isLoading).toBeFalse();
  });
});
