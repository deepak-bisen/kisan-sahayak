import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container" aria-live="polite">
      @for (t of toastSvc.toasts(); track t.id) {
        <div class="toast" [class.toast--success]="t.type === 'success'"
             [class.toast--error]="t.type === 'error'"
             [class.toast--info]="t.type === 'info'">
          <span class="toast__msg">{{ t.message }}</span>
          <button class="toast__close" (click)="toastSvc.dismiss(t.id)" aria-label="Dismiss">&times;</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 84px;
      right: 16px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 8px;
      max-width: 400px;
      pointer-events: none;
    }
    .toast {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 14px 18px;
      border-radius: var(--radius-md);
      box-shadow: 0 6px 24px rgba(0,0,0,0.12);
      font-size: 0.92rem;
      font-weight: 500;
      pointer-events: auto;
      animation: toast-in 0.25s var(--ease);
      border: 1px solid;
    }
    .toast--success { background: #e8f0e3; color: #2d4f1e; border-color: #b5d0a5; }
    .toast--error { background: #f5e6e0; color: #7a3a28; border-color: #e3c0b0; }
    .toast--info { background: #e6edf5; color: #1e3f6b; border-color: #b0c8e3; }
    .toast__msg { flex: 1; line-height: 1.4; }
    .toast__close {
      background: none;
      border: none;
      font-size: 1.3rem;
      line-height: 1;
      cursor: pointer;
      opacity: 0.6;
      padding: 0 2px;
      color: inherit;
    }
    .toast__close:hover { opacity: 1; }
    @keyframes toast-in {
      from { opacity: 0; transform: translateX(100%); }
      to { opacity: 1; transform: translateX(0); }
    }
  `],
})
export class ToastComponent {
  readonly toastSvc = inject(ToastService);
}
