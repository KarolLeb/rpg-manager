import { Component, OnInit, inject } from '@angular/core';

import { CharacterService } from '../../../core/services/character.service';
import { Character } from '../../../core/models/character.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-player-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="dashboard-container">
      <h1>Player Dashboard</h1>
      <section class="characters-section">
        <h2>My Characters</h2>
        @if (isLoading) {
          <div class="loading">Loading characters...</div>
        }
        @if (error) {
          <div class="error">{{ error }}</div>
        }
    
        @if (!isLoading && !error) {
          <div class="character-grid">
            @for (char of characters; track char) {
              <div class="character-card">
                <h3>{{ char.name }}</h3>
                <p>{{ char.characterClass }} - Level {{ char.level }}</p>
                <a [routerLink]="['/character', char.id]" class="btn">View Sheet</a>
              </div>
            }
            @if (characters.length === 0) {
              <div class="no-data">
                You don't have any characters yet.
              </div>
            }
          </div>
        }
      </section>
    </div>
    `,
  styles: [`
    .dashboard-container { padding: 20px; }
    .character-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; margin-top: 20px; }
    .character-card { border: 1px solid #ccc; padding: 15px; border-radius: 8px; }
    .btn { display: inline-block; margin-top: 10px; padding: 8px 16px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
  `]
})
export class PlayerDashboardComponent implements OnInit {
  private readonly characterService = inject(CharacterService);

  characters: Character[] = [];
  isLoading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.characterService.getCharacters().subscribe({
      next: (data) => {
        this.characters = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load characters.';
        this.isLoading = false;
      }
    });
  }
}
