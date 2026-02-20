import { Component, OnInit, inject } from '@angular/core';

import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AttributeCardComponent } from '../attribute-card/attribute-card.component';
import { AttributeConfig } from './models/character-data.model';
import { CharacterService } from '../../core/services/character.service';
import { ToastService } from '../../core/services/toast.service';
import { Character } from '../../core/models/character.model';
import { ActivatedRoute } from '@angular/router';
import { DUMMY_CHARACTER_DATA } from './character-dummy-data';

@Component({
  selector: 'app-character-sheet',
  standalone: true,
  imports: [ReactiveFormsModule, AttributeCardComponent],
  templateUrl: './character-sheet.component.html',
  styleUrls: ['./character-sheet.component.scss']
})
export class CharacterSheetPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly characterService = inject(CharacterService);
  private readonly toastService = inject(ToastService);
  private readonly route = inject(ActivatedRoute);

  characterForm: FormGroup;
  currentCharacterId?: number;
  isLoading = true;

  physicalAttributes: AttributeConfig[] = [
    { key: 'strength', label: 'Siła' },
    { key: 'constitution', label: 'Wytrzymałość' },
    { key: 'dexterity', label: 'Zręczność' },
    { key: 'agility', label: 'Zwinność' },
    { key: 'perception', label: 'Percepcja' },
    { key: 'empathy', label: 'Empatia' }
  ];

  mentalAttributes: AttributeConfig[] = [
    { key: 'charisma', label: 'Charyzma' },
    { key: 'intelligence', label: 'Inteligencja' },
    { key: 'knowledge', label: 'Wiedza' },
    { key: 'willpower', label: 'Siła Woli' }
  ];

  constructor() {
    this.characterForm = this.fb.group({
      info: this.fb.group({
        name: [''],
        profession: [''],
        ambition: [''],
        nemesis: ['']
      }),
      attributes: this.fb.group({
      })
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.currentCharacterId = +id;
      this.characterService.getCharacter(this.currentCharacterId).subscribe({
        next: (character: Character) => {
          this.loadCharacterData(character);
          this.isLoading = false;
        },
        error: (err: any) => {
          console.error('Failed to load character', err);
          this.toastService.error('Failed to load character from server. Loading dummy data.');
          this.loadDummyData();
          this.isLoading = false;
        }
      });
    } else {
      this.loadDummyData();
      this.isLoading = false;
    }
  }

  onSave() {
    if (!this.currentCharacterId) {
      this.toastService.warning('Cannot save character: No character ID found.');
      return;
    }

    const formVal = this.characterForm.value;
    const attributesRaw = formVal.attributes;
    const attributesToSave: any = {};

    // Map 'value' from form back to 'val' for DB storage
    Object.keys(attributesRaw).forEach(key => {
      const attr = attributesRaw[key];
      attributesToSave[key] = {
        val: attr.value,
        skills: attr.skills.map((s: any) => [s.name, s.level, s.total]) // Ensure skills are just arrays of values, not objects if form structure changed
      };
    });

    const characterToSave: Character = {
      id: this.currentCharacterId,
      name: formVal.info.name,
      characterClass: formVal.info.profession,
      level: 1,
      stats: JSON.stringify(attributesToSave)
    };

    this.characterService.updateCharacter(this.currentCharacterId, characterToSave).subscribe({
      next: (res) => {
        console.log('Character saved!', res);
        this.toastService.success('Character saved successfully!');
      },
      error: (err) => {
        console.error('Save failed', err);
        this.toastService.error('Error saving character. Please try again.');
      }
    });
  }

  private loadCharacterData(character: Character) {
    this.characterForm.get('info')?.patchValue({
      name: character.name,
      profession: character.characterClass,
      ambition: 'Z bazy danych',
      nemesis: 'Brak danych'
    });

    let attributesData: any = {};
    if (character.stats) {
      try {
        const parsed = JSON.parse(character.stats);
        if (parsed && typeof parsed === 'object') {
          attributesData = parsed;
        } else if (parsed !== null) {
          console.error('Character stats is not an object', parsed);
        }
      } catch (e) {
        console.error('Failed to parse character stats', e);
      }
    }

    const attrsGroup = this.characterForm.get('attributes') as FormGroup;
    const dataKeys = Object.keys(attributesData);
    
    dataKeys.forEach(key => {
      const data = attributesData[key];
      if (data && typeof data === 'object' && Array.isArray(data.skills)) {
        const skillsArray = this.fb.array(
          data.skills.map((s: any[]) => this.fb.group({
            name: [s[0]],
            level: [s[1]],
            total: [s[2]]
          }))
        );

        attrsGroup.addControl(key, this.fb.group({
          value: [data.val],
          skills: skillsArray
        }));
      }
    });
  }

  private loadDummyData() {
    this.characterForm.get('info')?.patchValue(DUMMY_CHARACTER_DATA.info);

    const attributesData = DUMMY_CHARACTER_DATA.attributes;
    const attrsGroup = this.characterForm.get('attributes') as FormGroup;

    Object.keys(attributesData).forEach(key => {
      const data = attributesData[key];

      const skillsArray = this.fb.array(
        data.skills.map((s: any[]) => this.fb.group({
          name: [s[0]],
          level: [s[1]],
          total: [s[2]]
        }))
      );

      attrsGroup.addControl(key, this.fb.group({
        value: [data.val],
        skills: skillsArray
      }));
    });
  }

  getAttributeGroup(key: string): FormGroup {
    return this.characterForm.get(['attributes', key]) as FormGroup;
  }
}
