import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CharacterSheetPageComponent } from './character-sheet.component';
import { CharacterService } from '../../core/services/character.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { Character } from '../../core/models/character.model';
import { provideRouter } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

describe('CharacterSheetPageComponent', () => {
  let component: CharacterSheetPageComponent;
  let fixture: ComponentFixture<CharacterSheetPageComponent>;
  let mockCharacterService: jasmine.SpyObj<CharacterService>;
  let routeId: string | null = '1';

  const dummyCharacter: Character = {
    id: 1,
    name: 'Test Char',
    characterClass: 'Soldier',
    level: 1,
    stats: JSON.stringify({
      strength: { val: 10, skills: [['Melee', 2, 12]] },
      charisma: { val: 8, skills: [['Persuasion', 4, 12]] }
    })
  };

  beforeEach(async () => {
    routeId = '1'; // Default
    mockCharacterService = jasmine.createSpyObj('CharacterService', ['getCharacter', 'getCharacters', 'updateCharacter']);
    mockCharacterService.getCharacter.and.returnValue(of(dummyCharacter));
    mockCharacterService.getCharacters.and.returnValue(of([dummyCharacter]));
    mockCharacterService.updateCharacter.and.returnValue(of(dummyCharacter));

    await TestBed.configureTestingModule({
      imports: [CharacterSheetPageComponent, ReactiveFormsModule],
      providers: [
        { provide: CharacterService, useValue: mockCharacterService },
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => routeId } }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharacterSheetPageComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load dummy data when no id is provided', () => {
    routeId = null;
    fixture.detectChanges(); // ngOnInit
    expect(component.currentCharacterId).toBeUndefined();
    expect(component.characterForm.get('info.name')?.value).toBe('Tonny Ballony');
  });

  it('should load character data on init when id is provided', () => {
    fixture.detectChanges();
    expect(component.currentCharacterId).toBe(1);
    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
    
    // Check if attribute groups are created
    const strengthGroup = component.characterForm.get('attributes.strength');
    expect(strengthGroup).toBeTruthy();
    expect(strengthGroup?.get('value')?.value).toBe(10);
  });

  it('should call updateCharacter on save', () => {
    fixture.detectChanges();
    // Modify value
    component.characterForm.get('info.name')?.setValue('Updated Char');
    
    spyOn(window, 'alert');
    component.onSave();

    expect(mockCharacterService.updateCharacter).toHaveBeenCalled();
    const args = mockCharacterService.updateCharacter.calls.mostRecent().args;
    expect(args[0]).toBe(1);
    expect(args[1].name).toBe('Updated Char');
    
    // Check if stats are correctly serialized back
    const savedStats = JSON.parse(args[1].stats);
    expect(savedStats.strength.val).toBe(10);
    expect(savedStats.strength.skills[0]).toEqual(['Melee', 2, 12]);
  });

  it('should load dummy data when getCharacter fails', () => {
    mockCharacterService.getCharacter.and.returnValue(throwError(() => new Error('Not found')));
    
    fixture.detectChanges();

    expect(component.characterForm.get('info.name')?.value).toBe('Tonny Ballony');
    expect(component.characterForm.get('attributes.strength')?.get('value')?.value).toBe(12);
  });

  it('should not call updateCharacter if currentCharacterId is missing', () => {
    fixture.detectChanges();
    component.currentCharacterId = undefined;
    component.onSave();
    expect(mockCharacterService.updateCharacter).not.toHaveBeenCalled();
  });

  it('should handle save error', () => {
    fixture.detectChanges();
    mockCharacterService.updateCharacter.and.returnValue(throwError(() => new Error('Save failed')));
    spyOn(console, 'error');
    spyOn(window, 'alert');

    component.onSave();

    expect(console.error).toHaveBeenCalledWith('Save failed', jasmine.any(Error));
    expect(window.alert).toHaveBeenCalledWith('Błąd podczas zapisywania postaci.');
  });

  it('should handle invalid stats JSON', () => {
    const invalidChar = { ...dummyCharacter, stats: '{invalid' };
    mockCharacterService.getCharacter.and.returnValue(of(invalidChar));
    spyOn(console, 'error');
    
    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Failed to parse character stats', jasmine.any(Error));
  });

  it('should return attribute group', () => {
    fixture.detectChanges();
    const group = component.getAttributeGroup('strength');
    expect(group).toBeTruthy();
    expect(group.get('value')).toBeTruthy();
  });
});