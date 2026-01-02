import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-attribute-card',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './attribute-card.component.html',
  styleUrls: ['./attribute-card.component.scss']
})
export class AttributeCardComponent {
  @Input({ required: true }) title!: string; 
  @Input({ required: true }) parentForm!: FormGroup;
  @Input() attributeControlName: string = 'value';
  @Input() skillsArrayName: string = 'skills'; 
  get skillsControls() {
    return (this.parentForm.get(this.skillsArrayName) as any)?.controls || [];
  }
}