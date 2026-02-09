import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CharacterSheetPageComponent } from './character-sheet.component';
import { CharacterService } from '../../core/services/character.service';
import { of, throwError, delay, switchMap } from 'rxjs';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
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
    expect(component.isLoading).toBeFalse();
  });

  it('should load dummy data when no id is provided and verify basic info', () => {
    routeId = null;
    fixture.detectChanges(); // ngOnInit
    expect(component.currentCharacterId).toBeUndefined();
    expect(component.isLoading).toBeFalse();

    const info = component.characterForm.get('info')?.value;
    expect(info.name).toBe('Tonny Ballony');
    expect(info.profession).toBe('Kanciarz');
    expect(info.ambition).toBe('To dobry interes');
    expect(info.nemesis).toBe('Interes ponad wszystko');
  });

  it('should load dummy physical attributes correctly', () => {
    routeId = null;
    fixture.detectChanges();
    expect(component.physicalAttributes.length).toBe(6);
    const expectedPhysical = [
      { key: 'strength', label: 'Siła' },
      { key: 'constitution', label: 'Wytrzymałość' },
      { key: 'dexterity', label: 'Zręczność' },
      { key: 'agility', label: 'Zwinność' },
      { key: 'perception', label: 'Percepcja' },
      { key: 'empathy', label: 'Empatia' }
    ];
    expectedPhysical.forEach((expected, index) => {
      expect(component.physicalAttributes[index].key).toBe(expected.key);
      expect(component.physicalAttributes[index].label).toBe(expected.label);
      const group = component.getAttributeGroup(expected.key);
      expect(group).toBeTruthy();
      expect(group.get('value')?.value).toBe(12);
      expect((group.get('skills') as any).length).toBe(3);
    });

    const strength = component.getAttributeGroup('strength');
    const strengthSkills = strength.get('skills') as any;
    expect(strengthSkills.at(0).value).toEqual({ name: 'Broń biała', level: 5, total: 15 });
    expect(strengthSkills.at(1).value).toEqual({ name: 'Bijatyka', level: 5, total: 15 });
    expect(strengthSkills.at(2).value).toEqual({ name: 'Zastraszanie', level: 5, total: 15 });
  });

  it('should load dummy mental attributes correctly', () => {
    routeId = null;
    fixture.detectChanges();
    expect(component.mentalAttributes.length).toBe(4);
    const expectedMental = [
      { key: 'charisma', label: 'Charyzma' },
      { key: 'intelligence', label: 'Inteligencja' },
      { key: 'knowledge', label: 'Wiedza' },
      { key: 'willpower', label: 'Siła Woli' }
    ];
    expectedMental.forEach((expected, index) => {
      expect(component.mentalAttributes[index].key).toBe(expected.key);
      expect(component.mentalAttributes[index].label).toBe(expected.label);
      const group = component.getAttributeGroup(expected.key);
      expect(group).toBeTruthy();
      expect(group.get('value')?.value).toBe(12);
      expect((group.get('skills') as any).length).toBe(3);
    });

    const intelligence = component.getAttributeGroup('intelligence');
    const intelligenceSkills = intelligence.get('skills') as any;
    expect(intelligenceSkills.at(0).get('name')?.value).toBe('Analiza');
    expect(intelligenceSkills.at(1).get('name')?.value).toBe('Komputery');
    expect(intelligenceSkills.at(2).get('name')?.value).toBe('Taktyka');
  });

  it('should verify willpower skills dummy data', () => {
    routeId = null;
    fixture.detectChanges();
    const willpower = component.getAttributeGroup('willpower');
    const willpowerSkills = willpower.get('skills') as any;
    expect(willpowerSkills.at(0).get('name')?.value).toBe('Intuicja');
    expect(willpowerSkills.at(1).get('name')?.value).toBe('Koncentracja');
    expect(willpowerSkills.at(2).get('name')?.value).toBe('Siła Woli');
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
    
    const attrsGroup = component.characterForm.get('attributes') as FormGroup;
    expect(Object.keys(attrsGroup.controls).length).toBeGreaterThan(0);
  }));

  it('should call updateCharacter on save with correctly serialized stats', () => {
    fixture.detectChanges();
    // Modify value
    component.characterForm.get('info.name')?.setValue('Updated Char');

    spyOn(window, 'alert');
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
    expect(savedStats.strength.skills[0]).toEqual(['Melee', 2, 12]);

    expect(console.log).toHaveBeenCalledWith('Character saved!', jasmine.any(Object));
    expect(window.alert).toHaveBeenCalledWith('Postać została zapisana pomyślnie!');
    expect(window.alert).not.toHaveBeenCalledWith('');
  });

  it('should load dummy data when getCharacter fails and stop loading', () => {
    mockCharacterService.getCharacter.and.returnValue(throwError(() => new Error('Not found')));
    spyOn(console, 'error');

    fixture.detectChanges();

    expect(console.error).toHaveBeenCalled();
    // Check specific dummy values to kill StringLiteral mutations
    expect(component.characterForm.get('info.name')?.value).toBe('Tonny Ballony');
    expect(component.characterForm.get('info.profession')?.value).toBe('Kanciarz');
    expect(component.characterForm.get('attributes.strength')?.get('value')?.value).toBe(12);
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
    expect(attrsGroup.get('valid')).toBeTruthy();
    expect(attrsGroup.get('invalid1')).toBeFalsy();
    expect(attrsGroup.get('invalid2')).toBeFalsy();
    expect(attrsGroup.get('invalid3')).toBeFalsy();
  });

  it('should handle save error and show alert', () => {
    fixture.detectChanges();
    mockCharacterService.updateCharacter.and.returnValue(throwError(() => new Error('Save failed')));
    spyOn(console, 'error');
    spyOn(window, 'alert');

    component.onSave();

    expect(console.error).toHaveBeenCalledWith('Save failed', jasmine.any(Error));
    expect(window.alert).toHaveBeenCalledWith('Błąd podczas zapisywania postaci.');
    expect(window.alert).not.toHaveBeenCalledWith('');
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