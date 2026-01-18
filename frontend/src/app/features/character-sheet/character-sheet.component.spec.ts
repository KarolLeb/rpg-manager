import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CharacterSheetPageComponent } from './character-sheet.component';
import { CharacterService } from '../../core/services/character.service';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { Character } from '../../core/models/character.model';
import { By } from '@angular/platform-browser';

describe('CharacterSheetPageComponent', () => {
  let component: CharacterSheetPageComponent;
  let fixture: ComponentFixture<CharacterSheetPageComponent>;
  let mockCharacterService: jasmine.SpyObj<CharacterService>;

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
    mockCharacterService = jasmine.createSpyObj('CharacterService', ['getCharacters', 'updateCharacter']);
    mockCharacterService.getCharacters.and.returnValue(of([dummyCharacter]));
    mockCharacterService.updateCharacter.and.returnValue(of(dummyCharacter));

    await TestBed.configureTestingModule({
      imports: [CharacterSheetPageComponent, ReactiveFormsModule],
      providers: [
        { provide: CharacterService, useValue: mockCharacterService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharacterSheetPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load character data on init', () => {
    expect(component.currentCharacterId).toBe(1);
    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
    
    // Check if attribute groups are created
    const strengthGroup = component.characterForm.get('attributes.strength');
    expect(strengthGroup).toBeTruthy();
    expect(strengthGroup?.get('value')?.value).toBe(10);
    
    // Check skills
    const skills = strengthGroup?.get('skills')?.value;
    expect(skills.length).toBe(1);
    expect(skills[0].name).toBe('Melee');
  });

  it('should call updateCharacter on save', () => {
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
});
