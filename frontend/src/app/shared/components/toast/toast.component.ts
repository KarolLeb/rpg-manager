import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="toast-item" [ngClass]="toast.type" (click)="remove(toast.id)">
          <div class="toast-icon">
            @switch (toast.type) {
              @case ('success') { <span class="material-icons">check_circle</span> }
              @case ('error') { <span class="material-icons">error</span> }
              @case ('warning') { <span class="material-icons">warning</span> }
              @case ('info') { <span class="material-icons">info</span> }
            }
          </div>
          <div class="toast-message">{{ toast.message }}</div>
          <button class="toast-close" (click)="remove(toast.id); $event.stopPropagation()">&times;</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: none;
    }

    .toast-item {
      pointer-events: auto;
      min-width: 250px;
      max-width: 400px;
      padding: 12px 16px;
      border-radius: 8px;
      background: white;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      display: flex;
      align-items: center;
      gap: 12px;
      cursor: pointer;
      animation: slideIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      position: relative;
      border-left: 4px solid #ccc;
    }

    .toast-item.success { border-left-color: #4caf50; color: #1b5e20; }
    .toast-item.error { border-left-color: #f44336; color: #b71c1c; }
    .toast-item.warning { border-left-color: #ff9800; color: #e65100; }
    .toast-item.info { border-left-color: #2196f3; color: #0d47a1; }

    .toast-icon {
      font-size: 20px;
      display: flex;
      align-items: center;
    }

    .toast-message {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      line-height: 1.4;
    }

    .toast-close {
      background: none;
      border: none;
      font-size: 20px;
      color: currentColor;
      opacity: 0.5;
      cursor: pointer;
      padding: 0 4px;
    }

    .toast-close:hover { opacity: 1; }

    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);

  remove(id: number) {
    this.toastService.remove(id);
  }
}
