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
    expect(titleEl.nativeElement.textContent.trim()).toBe('REFLEX');
    expect(titleEl.nativeElement.textContent).not.toContain('Reflex');
  });

  it('should have default values for control names', () => {
    const newComponent = new AttributeCardComponent();
    expect(newComponent.attributeControlName).toBe('value');
    expect(newComponent.skillsArrayName).toBe('skills');
    expect(newComponent.attributeControlName).not.toBe('');
    expect(newComponent.skillsArrayName).not.toBe('');
  });

  it('should return empty array if skills control is missing', () => {
    component.skillsArrayName = 'nonExistent';
    expect(component.skillsControls).toEqual([]);
    expect(component.skillsControls.length).toBe(0);
    expect(component.skillsControls).not.toBeNull();
    expect(component.skillsControls).not.toBeUndefined();
  });
});
