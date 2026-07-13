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
})
export class MyListingsComponent implements OnInit {
  readonly listings = signal<EquipmentDTO[]>([]);
  readonly loading = signal(false);

  constructor(private svc: EquipmentService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user) return;
    this.load(user.userId!);
  }

  load(ownerId: string) {
    this.loading.set(true);
    this.svc.getByOwner(ownerId).subscribe({ next: (r: EquipmentDTO[]) => { this.listings.set(r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  edit(id: string) { this.router.navigate(['/marketplace', id, 'edit']); }
  delete(id: string) { this.svc.delete(id).subscribe({ next: () => this.load(this.auth.currentUser()!.userId!), error: () => {} }); }
}
