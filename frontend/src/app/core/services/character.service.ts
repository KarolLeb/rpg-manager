import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Character } from '../models/character.model';

@Injectable({
  providedIn: 'root'
})
export class CharacterService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/characters';

  getCharacters(): Observable<Character[]> {
    return this.http.get<Character[]>(this.apiUrl);
  }

  getCharacter(uuid: string): Observable<Character> {
    return this.http.get<Character>(`${this.apiUrl}/${uuid}`);
  }

  updateCharacter(uuid: string, character: Character): Observable<Character> {
    return this.http.put<Character>(`${this.apiUrl}/${uuid}`, character);
  }
}
