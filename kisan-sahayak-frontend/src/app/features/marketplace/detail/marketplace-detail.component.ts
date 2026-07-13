import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EquipmentService } from '../../../core/services/equipment.service';
import { EquipmentDTO } from '../../../core/models/equipment.model';
import { BookingService } from '../../../core/services/booking.service';
import { AuthService } from '../../../core/services/auth.service';
import { BookingDTO } from '../../../core/models/booking.model';

@Component({
  selector: 'app-marketplace-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './marketplace-detail.component.html',
})
export class MarketplaceDetailComponent implements OnInit {
  readonly equipment = signal<EquipmentDTO | null>(null);
  readonly loading = signal(false);
  readonly bookingSuccess = signal(false);

  constructor(private route: ActivatedRoute, private svc: EquipmentService, private bookingSvc: BookingService, private auth: AuthService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.loading.set(true);
    this.svc.getById(id).subscribe({ next: (e) => { this.equipment.set(e); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  startDate: string | null = null;
  endDate: string | null = null;

  book(): void {
    const user = this.auth.currentUser();
    if (!user || !this.equipment()) return;

    const payload: BookingDTO = {
      equipmentId: this.equipment()!.equipmentId!,
      renterId: user.userId!,
      startDate: this.startDate || new Date().toISOString().slice(0,10),
      endDate: this.endDate || new Date().toISOString().slice(0,10)
    };

    this.bookingSvc.create(payload).subscribe({ next: () => { this.bookingSuccess.set(true); }, error: () => {} });
  }
}
