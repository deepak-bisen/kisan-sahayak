import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EquipmentService } from '../../../core/services/equipment.service';
import { AuthService } from '../../../core/services/auth.service';
import { EquipmentDTO } from '../../../core/models/equipment.model';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-my-listings',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-listings.component.html',
  styles: [`
    .dialog-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.5); display:flex; align-items:center; justify-content:center; z-index:1000; }
    .dialog-box { background:#fff; padding:24px; border-radius:12px; max-width:420px; width:90%; box-shadow:0 8px 32px rgba(0,0,0,0.2); }
    .dialog-box h3 { margin:0 0 8px; font-size:1.1rem; color:#1a1a2e; }
    .dialog-box .field-hint { margin:0 0 20px; }
    .dialog-actions { display:flex; gap:12px; justify-content:flex-end; }
    .listing-actions { margin-top:8px; display:flex; align-items:center; justify-content:space-between; gap:12px; }
    .listing-buttons { display:flex; gap:8px; }
    .toggle { display:inline-flex; align-items:center; gap:8px; cursor:pointer; user-select:none; }
    .toggle--loading { opacity:0.6; pointer-events:none; }
    .toggle input { position:absolute; opacity:0; width:0; height:0; }
    .toggle__slider { position:relative; width:36px; height:20px; background:var(--color-clay); border-radius:10px; transition:background .2s; flex-shrink:0; }
    .toggle__slider::before { content:''; position:absolute; top:2px; left:2px; width:16px; height:16px; background:#fff; border-radius:50%; transition:transform .2s; }
    .toggle input:checked + .toggle__slider { background:var(--color-leaf); }
    .toggle input:checked + .toggle__slider::before { transform:translateX(16px); }
    .toggle__label { font-size:0.82rem; color:var(--color-cream-dim); }
  `],
})
export class MyListingsComponent implements OnInit {
  readonly listings = signal<EquipmentDTO[]>([]);
  readonly loading = signal(false);
  readonly deletingId = signal<string | null>(null);
  readonly confirmDelete = signal<EquipmentDTO | null>(null);
  readonly togglingId = signal<string | null>(null);

  constructor(public svc: EquipmentService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user) return;
    this.load(user.userId!);
  }

  load(ownerId: string) {
    this.loading.set(true);
    this.svc.getByOwner(ownerId).subscribe({
      next: (r: EquipmentDTO[]) => { this.listings.set(r || []); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  edit(id: string) { this.router.navigate(['/marketplace', id, 'edit']); }

  promptDelete(e: EquipmentDTO) { this.confirmDelete.set(e); }

  cancelDelete() { this.confirmDelete.set(null); }

  doDelete(id: string) {
    this.deletingId.set(id);
    this.confirmDelete.set(null);
    this.svc.delete(id).subscribe({
      next: () => { this.deletingId.set(null); this.load(this.auth.currentUser()!.userId!); },
      error: () => this.deletingId.set(null),
    });
  }

  toggleAvailability(id: string): void {
    this.togglingId.set(id);
    this.svc.toggleAvailability(id).subscribe({
      next: () => {
        this.listings.update((items) =>
          items.map((e) =>
            e.equipmentId === id ? { ...e, isAvailable: !e.isAvailable } : e
          )
        );
        this.togglingId.set(null);
      },
      error: () => this.togglingId.set(null),
    });
  }
}
