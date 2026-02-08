import { Component, OnInit, inject } from '@angular/core';

import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AttributeCardComponent } from '../attribute-card/attribute-card.component';
import { AttributeConfig } from './models/character-data.model';
import { CharacterService } from '../../core/services/character.service';
import { Character } from '../../core/models/character.model';
import { ActivatedRoute } from '@angular/router';

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
    if (!this.currentCharacterId) return;

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
        alert('Postać została zapisana pomyślnie!');
      },
      error: (err) => {
        console.error('Save failed', err);
        alert('Błąd podczas zapisywania postaci.');
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

    let attributesData: any;
    try {
      const parsed = JSON.parse(character.stats || '{}');
      if (parsed && typeof parsed === 'object') {
        attributesData = parsed;
      } else {
        if (character.stats && parsed !== null) {
          console.error('Character stats is not an object', parsed);
        }
        attributesData = {};
      }
    } catch (e) {
      console.error('Failed to parse character stats', e);
      attributesData = {};
    }

    const attrsGroup = this.characterForm.get('attributes') as FormGroup;
    const dataKeys = Object.keys(attributesData);
    
    dataKeys.forEach(key => {
      const data = attributesData[key];
      if (typeof data === 'object' && data.skills) {
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
    this.characterForm.get('info')?.patchValue({
      name: 'Tonny Ballony',
      profession: 'Kanciarz',
      ambition: 'To dobry interes',
      nemesis: 'Interes ponad wszystko'
    });

    const attributesData: any = {
      strength: { val: 12, skills: [['Broń biała', 5, 15], ['Bijatyka', 5, 15], ['Zastraszanie', 5, 15]] },
      constitution: { val: 12, skills: [['Atletyka', 5, 15], ['Mocna Głowa', 5, 15], ['Odporność', 5, 15]] },
      dexterity: { val: 12, skills: [['Parowanie', 5, 15], ['Pilotowanie', 5, 15], ['Sztuka', 5, 15]] },
      agility: { val: 12, skills: [['Akrobatyka', 5, 15], ['Rzucanie', 5, 15], ['Skradanie', 5, 15]] },
      perception: { val: 12, skills: [['Broń Długa', 5, 15], ['Nawigacja', 5, 15], ['Spostrzegawczość', 5, 15]] },
      empathy: { val: 12, skills: [['Blef', 5, 15], ['Gadanina', 5, 15], ['Oswajanie', 5, 15]] },

      charisma: { val: 12, skills: [['Aktorstwo', 5, 15], ['Handel', 5, 15], ['Przekonywanie', 5, 15]] },
      intelligence: { val: 12, skills: [['Analiza', 5, 15], ['Komputery', 5, 15], ['Taktyka', 5, 15]] },
      knowledge: { val: 12, skills: [['Kosmos', 5, 15], ['Kultura', 5, 15], ['Medycyna', 5, 15]] },
      willpower: { val: 12, skills: [['Intuicja', 5, 15], ['Koncentracja', 5, 15], ['Siła Woli', 5, 15]] }
    };

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
