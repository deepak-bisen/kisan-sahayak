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
  `],
})
export class MyListingsComponent implements OnInit {
  readonly listings = signal<EquipmentDTO[]>([]);
  readonly loading = signal(false);
  readonly deletingId = signal<string | null>(null);
  readonly confirmDelete = signal<EquipmentDTO | null>(null);

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
}
