import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EquipmentService } from '../../../core/services/equipment.service';
import { AuthService } from '../../../core/services/auth.service';
import { EquipmentDTO } from '../../../core/models/equipment.model';
import { BreadcrumbComponent } from '../../../shared/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-marketplace-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, BreadcrumbComponent],
  templateUrl: './marketplace-list.component.html',
})
export class MarketplaceListComponent implements OnInit {
  readonly equipments = signal<EquipmentDTO[]>([]);
  readonly loading = signal(false);
  readonly loadError = signal<string | null>(null);

  readonly searchQuery = signal('');
  readonly selectedCategory = signal('');
  readonly selectedVillage = signal('');
  readonly sortBy = signal<'name-asc' | 'name-desc' | 'price-asc' | 'price-desc'>('name-asc');
  readonly availableOnly = signal(false);

  readonly categories = computed(() => {
    const seen = new Set<string>();
    const cats: string[] = [];
    for (const e of this.equipments()) {
      if (!seen.has(e.category)) {
        seen.add(e.category);
        cats.push(e.category);
      }
    }
    return cats.sort();
  });

  readonly villages = computed(() => {
    const seen = new Set<string>();
    const v: string[] = [];
    for (const e of this.equipments()) {
      const name = e.villageName || e.district || '';
      if (name && !seen.has(name)) {
        seen.add(name);
        v.push(name);
      }
    }
    return v.sort();
  });

  readonly filtered = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const cat = this.selectedCategory();
    const village = this.selectedVillage();
    const avail = this.availableOnly();
    const sort = this.sortBy();

    let result = this.equipments().filter((e) => {
      if (query && !e.name.toLowerCase().includes(query) && !(e.description || '').toLowerCase().includes(query)) return false;
      if (cat && e.category !== cat) return false;
      if (village && (e.villageName || e.district) !== village) return false;
      if (avail && !e.isAvailable) return false;
      return true;
    });

    switch (sort) {
      case 'price-asc': result = result.sort((a, b) => Number(a.dailyRate) - Number(b.dailyRate)); break;
      case 'price-desc': result = result.sort((a, b) => Number(b.dailyRate) - Number(a.dailyRate)); break;
      case 'name-asc': result = result.sort((a, b) => a.name.localeCompare(b.name)); break;
      case 'name-desc': result = result.sort((a, b) => b.name.localeCompare(a.name)); break;
    }

    return result;
  });

  constructor(public svc: EquipmentService, private router: Router, public auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.svc.getAll().subscribe({
      next: (r) => {
        this.equipments.set(r || []);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.loadError.set('Could not load equipment. Check your connection.');
      },
    });
  }
}
