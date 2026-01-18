import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AttributeCardComponent } from './attribute-card.component';
import { FormControl, FormGroup, ReactiveFormsModule, FormArray } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('AttributeCardComponent', () => {
  let component: AttributeCardComponent;
  let fixture: ComponentFixture<AttributeCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttributeCardComponent, ReactiveFormsModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttributeCardComponent);
    component = fixture.componentInstance;
    
    // Setup inputs with expected form structure
    // Template expects skill to have 'name', 'level', 'total'
    component.title = 'Reflex';
    component.parentForm = new FormGroup({
      baseVal: new FormControl(8), // custom name
      mySkills: new FormArray([
        new FormGroup({ 
          name: new FormControl('Evasion'), 
          level: new FormControl(4),
          total: new FormControl(12)
        }),
        new FormGroup({ 
          name: new FormControl('Driving'), 
          level: new FormControl(2),
          total: new FormControl(10)
        })
      ])
    });
    component.attributeControlName = 'baseVal';
    component.skillsArrayName = 'mySkills';

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the uppercase title', () => {
    const titleEl = fixture.debugElement.query(By.css('.title'));
    expect(titleEl.nativeElement.textContent).toContain('REFLEX');
  });

  it('should bind attribute input to form control', () => {
    const inputEl = fixture.debugElement.query(By.css('.attribute-value-wrapper input')).nativeElement;
    expect(inputEl.value).toBe('8');
    
    inputEl.value = '9';
    inputEl.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(component.parentForm.get('baseVal')?.value).toBe(9);
  });

  it('should render skill rows', () => {
    const rows = fixture.debugElement.queryAll(By.css('.skill-row'));
    expect(rows.length).toBe(2);
    
    expect(rows[0].nativeElement.textContent).toContain('Evasion');
    expect(rows[0].nativeElement.textContent).toContain('12'); // Total
  });
});
