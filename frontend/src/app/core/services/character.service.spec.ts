import { TestBed, fakeAsync, tick } from '@angular/core/testing';
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

  it('should fetch characters from correct URL', fakeAsync(() => {
    const mockCharacters: Character[] = [
      { id: 1, name: 'Char 1', characterClass: 'Class 1', level: 1, stats: '{}' }
    ];

    service.getCharacters().subscribe(characters => {
      expect(characters).toEqual(mockCharacters);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/characters');
    expect(req.request.method).toBe('GET');
    req.flush(mockCharacters);
    tick();
  }));

  it('should update character with correct URL and method', fakeAsync(() => {
    const mockCharacter: Character = { id: 1, name: 'Updated Char', characterClass: 'Class 1', level: 1, stats: '{}' };

    service.updateCharacter(1, mockCharacter).subscribe(character => {
      expect(character).toEqual(mockCharacter);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/characters/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockCharacter);
    req.flush(mockCharacter);
    tick();
  }));

  it('should fetch single character with correct URL', fakeAsync(() => {
    const mockCharacter: Character = { id: 1, name: 'Char 1', characterClass: 'Class 1', level: 1, stats: '{}' };

    service.getCharacter(1).subscribe(character => {
      expect(character).toEqual(mockCharacter);
    });

    const req = httpMock.expectOne(request => request.url === 'http://localhost:8080/api/characters/1');
    expect(req.request.method).toBe('GET');
    expect(req.request.url).toBe('http://localhost:8080/api/characters/1');
    req.flush(mockCharacter);
    tick();
  }));

  it('should use base apiUrl correctly in all methods', fakeAsync(() => {
    const id = 999;
    
    service.getCharacters().subscribe();
    const req1 = httpMock.expectOne('http://localhost:8080/api/characters');
    expect(req1.request.url).toBe('http://localhost:8080/api/characters');
    req1.flush([]);
    
    service.getCharacter(id).subscribe();
    const req2 = httpMock.expectOne(`http://localhost:8080/api/characters/${id}`);
    expect(req2.request.url).toBe(`http://localhost:8080/api/characters/${id}`);
    req2.flush({});
    
    service.updateCharacter(id, {} as any).subscribe();
    const req3 = httpMock.expectOne(`http://localhost:8080/api/characters/${id}`);
    expect(req3.request.url).toBe(`http://localhost:8080/api/characters/${id}`);
    expect(req3.request.method).toBe('PUT');
    req3.flush({});
    
    tick();
  }));
});
