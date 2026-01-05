import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormArray } from '@angular/forms';
import { AttributeCardComponent } from '../attribute-card/attribute-card.component';
import { AttributeConfig } from './models/character-data.model';
import { CharacterService } from '../../core/services/character.service';
import { Character } from '../../core/models/character.model';

@Component({
  selector: 'app-character-sheet',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AttributeCardComponent],
  templateUrl: './character-sheet.component.html',
  styleUrls: ['./character-sheet.component.scss']
})
export class CharacterSheetPageComponent implements OnInit {
  characterForm: FormGroup;
  currentCharacterId?: number;
  
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

  constructor(private fb: FormBuilder, private characterService: CharacterService) {
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
    this.characterService.getCharacters().subscribe(characters => {
      if (characters.length > 0) {
        const character = characters[0];
        this.currentCharacterId = character.id;
        this.loadCharacterData(character);
      } else {
        this.loadDummyData();
      }
    });
  }

  onSave() {
    if (!this.currentCharacterId) return;

    const formVal = this.characterForm.value;
    
    // Map internal structure back to the format expected by the DB (stats as JSON)
    const characterToSave: Character = {
      id: this.currentCharacterId,
      name: formVal.info.name,
      characterClass: formVal.info.profession,
      level: 1, // Default or fetch from form if added
      stats: JSON.stringify(formVal.attributes)
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
      attributesData = JSON.parse(character.stats);
    } catch (e) {
      console.error('Failed to parse character stats', e);
      return;
    }

    const attrsGroup = this.characterForm.get('attributes') as FormGroup;

    Object.keys(attributesData).forEach(key => {
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