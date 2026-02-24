import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CharacterSheetPageComponent } from './character-sheet.component';
import { CharacterService } from '../../core/services/character.service';
import { of, throwError, delay, switchMap } from 'rxjs';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Character } from '../../core/models/character.model';
import { provideRouter } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ToastService } from '../../core/services/toast.service';

describe('CharacterSheetPageComponent', () => {
  let component: CharacterSheetPageComponent;
  let fixture: ComponentFixture<CharacterSheetPageComponent>;
  let mockCharacterService: jasmine.SpyObj<CharacterService>;
  let mockToastService: jasmine.SpyObj<ToastService>;
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
    mockToastService = jasmine.createSpyObj('ToastService', ['success', 'error', 'warning']);

    await TestBed.configureTestingModule({
      imports: [CharacterSheetPageComponent, ReactiveFormsModule],
      providers: [
        { provide: CharacterService, useValue: mockCharacterService },
        { provide: ToastService, useValue: mockToastService },
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: (key: string) => key === 'id' ? routeId : null } }
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
    expect(component.isLoading).toBeFalse();
  });

  it('should have empty initial form values', () => {
    // We need a fresh component without ngOnInit execution or before it completes
    const fixture2 = TestBed.createComponent(CharacterSheetPageComponent);
    const comp2 = fixture2.componentInstance;
    expect(comp2.characterForm.get('info.name')?.value).toBe('');
    expect(comp2.characterForm.get('info.profession')?.value).toBe('');
    expect(comp2.characterForm.get('info.ambition')?.value).toBe('');
    expect(comp2.characterForm.get('info.nemesis')?.value).toBe('');
  });

  it('should not load dummy data when no id is provided', () => {
    routeId = null;
    fixture.detectChanges(); // ngOnInit
    expect(component.currentCharacterId).toBeUndefined();
    expect(component.isLoading).toBeFalse();

    const info = component.characterForm.get('info')?.value;
    expect(info.name).toBe('');
    expect(info.profession).toBe('');
  });

  it('should load character data on init when id is provided and manage isLoading correctly', fakeAsync(() => {
    // Setup a delayed response
    mockCharacterService.getCharacter.and.returnValue(of(dummyCharacter).pipe(delay(100)));

    // Before ngOnInit
    expect(component.isLoading).toBeTrue();

    fixture.detectChanges(); // calls ngOnInit

    expect(component.currentCharacterId).toBe(1);
    expect(component.isLoading).toBeTrue(); // Still true because of delay

    tick(100); // Complete observable

    expect(component.isLoading).toBeFalse();

    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
    expect(component.characterForm.get('info.ambition')?.value).toBe('Z bazy danych');
    expect(component.characterForm.get('info.nemesis')?.value).toBe('Brak danych');

    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBeGreaterThan(0);
  }));

  it('should call updateCharacter on save with correctly serialized stats', () => {
    fixture.detectChanges();
    // Modify value
    component.characterForm.get('info.name')?.setValue('Updated Char');

    spyOn(console, 'log');
    component.onSave();

    expect(mockCharacterService.updateCharacter).toHaveBeenCalled();
    const args = mockCharacterService.updateCharacter.calls.mostRecent().args;
    expect(args[0]).toBe(1);
    expect(args[1].name).toBe('Updated Char');
    expect(args[1].id).toBe(1);

    // Check if stats are correctly serialized back
    const savedStats = JSON.parse(args[1].stats);
    expect(savedStats.strength.val).toBe(10);
    expect(savedStats.strength.skills.length).toBe(1);
    expect(savedStats.strength.skills[0]).toEqual(['Melee', 2, 12]);
    // Extra checks to kill mutations in mapping
    expect(savedStats.strength.skills[0][0]).toBe('Melee');
    expect(savedStats.strength.skills[0][1]).toBe(2);
    expect(savedStats.strength.skills[0][2]).toBe(12);

    expect(console.log).toHaveBeenCalledWith('Character saved!', jasmine.any(Object));
    expect(mockToastService.success).toHaveBeenCalledWith('Character saved successfully!');
  });

  it('should NOT call updateCharacter on save if characterId is missing and show warning', () => {
    fixture.detectChanges();
    component.currentCharacterId = undefined;
    component.onSave();
    expect(mockCharacterService.updateCharacter).not.toHaveBeenCalled();
    expect(mockToastService.warning).toHaveBeenCalledWith('Cannot save character: No character ID found.');
  });

  it('should NOT load dummy data when getCharacter fails and stop loading', () => {
    mockCharacterService.getCharacter.and.returnValue(throwError(() => new Error('Not found')));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Failed to load character', jasmine.any(Error));
    expect(component.characterForm.get('info.name')?.value).toBe('');
    expect(component.isLoading).toBeFalse();
  });

  it('should handle missing or empty stats correctly and NOT log error', () => {
    const charWithNoStats = { ...dummyCharacter, stats: '' };
    mockCharacterService.getCharacter.and.returnValue(of(charWithNoStats));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
    expect(component.characterForm.get('info.ambition')?.value).toBe('Z bazy danych');
    expect(component.characterForm.get('info.nemesis')?.value).toBe('Brak danych');

    // attributes group should be empty
    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBe(0);
  });

  it('should handle null stats correctly and NOT log error', () => {
    const charWithNullStats = { ...dummyCharacter, stats: null as any };
    mockCharacterService.getCharacter.and.returnValue(of(charWithNullStats));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
  });

  it('should handle undefined stats correctly', () => {
    const charWithUndefinedStats = { ...dummyCharacter, stats: undefined as any };
    mockCharacterService.getCharacter.and.returnValue(of(charWithUndefinedStats));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(component.characterForm.get('info.name')?.value).toBe('Test Char');
  });

  it('should handle stats that are JSON null correctly', () => {
    const charWithJsonNull = { ...dummyCharacter, stats: 'null' };
    mockCharacterService.getCharacter.and.returnValue(of(charWithJsonNull));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).not.toHaveBeenCalled();
    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBe(0);
  });

  it('should handle stats that are not an object (e.g. number) correctly and log error', () => {
    const charWithNumberStats = { ...dummyCharacter, stats: '123' };
    mockCharacterService.getCharacter.and.returnValue(of(charWithNumberStats));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Character stats is not an object', 123);
    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBe(0);
  });

  it('should NOT clear attributesData if it is already a valid object', fakeAsync(() => {
    // Valid stats with one attribute
    const validStats = JSON.stringify({
      testAttr: { val: 15, skills: [['Skill 1', 5, 20]] }
    });
    const charWithValidStats = { ...dummyCharacter, stats: validStats };
    mockCharacterService.getCharacter.and.returnValue(of(charWithValidStats));

    fixture.detectChanges(); // calls ngOnInit -> loadCharacterData
    tick();

    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBe(1);
    expect(attrsGroup.get('testAttr')).toBeTruthy();
    expect(attrsGroup.get('testAttr')?.get('value')?.value).toBe(15);

    const skills = attrsGroup.get('testAttr')?.get('skills') as any;
    expect(skills.length).toBe(1);
    expect(skills.at(0).get('name').value).toBe('Skill 1');
    expect(skills.at(0).get('level').value).toBe(5);
    expect(skills.at(0).get('total').value).toBe(20);
  }));

  it('should skip attribute if it is not an object or lacks skills (kills mutations at line 145)', () => {
    const weirdStats = JSON.stringify({
      valid: { val: 10, skills: [['Skill', 1, 11]] },
      invalid1: 'not-an-object',
      invalid2: { val: 10 }, // no skills
      invalid3: null
    });
    const charWithWeirdStats = { ...dummyCharacter, stats: weirdStats };
    mockCharacterService.getCharacter.and.returnValue(of(charWithWeirdStats));

    fixture.detectChanges();

    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBe(1);
    expect(attrsGroup.get('valid')).toBeTruthy();
    expect(attrsGroup.get('invalid1')).toBeFalsy();
    expect(attrsGroup.get('invalid2')).toBeFalsy();
    expect(attrsGroup.get('invalid3')).toBeFalsy();
  });

  it('should handle save error and show toast error', () => {
    fixture.detectChanges();
    mockCharacterService.updateCharacter.and.returnValue(throwError(() => new Error('Save failed')));
    spyOn(console, 'error');

    component.onSave();

    expect(console.error).toHaveBeenCalledWith('Save failed', jasmine.any(Error));
    expect(mockToastService.error).toHaveBeenCalledWith('Error saving character. Please try again.');
  });

  it('should handle invalid stats JSON', () => {
    const invalidChar = { ...dummyCharacter, stats: '{invalid' };
    mockCharacterService.getCharacter.and.returnValue(of(invalidChar));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Failed to parse character stats', jasmine.any(Error));
    expect(console.error).not.toHaveBeenCalledWith('', jasmine.any(Error));
    expect(component.isLoading).toBeFalse();
  });

  it('should return attribute group', () => {
    fixture.detectChanges();
    const group = component.getAttributeGroup('strength');
    expect(group).toBeTruthy();
    expect(group.get('value')).toBeTruthy();
  });
});