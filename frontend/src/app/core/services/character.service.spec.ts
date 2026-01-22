import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CharacterService } from './character.service';
import { Character } from '../models/character.model';

describe('CharacterService', () => {
  let service: CharacterService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CharacterService]
    });
    service = TestBed.inject(CharacterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch characters', () => {
    const mockCharacters: Character[] = [
      { uuid: '1', name: 'Char 1', characterClass: 'Class 1', level: 1, stats: '{}' }
    ];

    service.getCharacters().subscribe(characters => {
      expect(characters.length).toBe(1);
      expect(characters).toEqual(mockCharacters);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/characters');
    expect(req.request.method).toBe('GET');
    req.flush(mockCharacters);
  });

  it('should update character', () => {
    const mockCharacter: Character = { uuid: '1', name: 'Updated Char', characterClass: 'Class 1', level: 1, stats: '{}' };

    service.updateCharacter('1', mockCharacter).subscribe(character => {
      expect(character).toEqual(mockCharacter);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/characters/1');
    expect(req.request.method).toBe('PUT');
    req.flush(mockCharacter);
  });
});
