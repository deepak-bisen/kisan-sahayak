import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

export interface BreadcrumbItem {
  label: string;
  path?: string;
}

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <nav class="breadcrumb" aria-label="Breadcrumb">
      @for (item of items(); track $index) {
        <span class="breadcrumb__sep" aria-hidden="true">/</span>
        @if (item.path && !$last) {
          <a [routerLink]="item.path" class="breadcrumb__link">{{ item.label }}</a>
        } @else {
          <span class="breadcrumb__current" [attr.aria-current]="$last ? 'page' : null">{{ item.label }}</span>
        }
      }
    </nav>
  `,
  styles: [`
    .breadcrumb {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 0.85rem;
      color: var(--color-cream-dim);
      margin-bottom: 16px;
    }
    .breadcrumb__sep:first-child { display: none; }
    .breadcrumb__link {
      color: var(--color-gold);
      text-decoration: none;
    }
    .breadcrumb__link:hover { text-decoration: underline; }
    .breadcrumb__current { color: var(--color-cream-dim); }
  `],
})
export class BreadcrumbComponent {
  readonly items = input<BreadcrumbItem[]>([]);
}
