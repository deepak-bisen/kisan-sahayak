import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { EquipmentService } from '../../../core/services/equipment.service';
import { EquipmentDTO } from '../../../core/models/equipment.model';
import { BookingService } from '../../../core/services/booking.service';
import { AuthService } from '../../../core/services/auth.service';
import { BookingDTO } from '../../../core/models/booking.model';

@Component({
  selector: 'app-marketplace-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './marketplace-detail.component.html',
  styleUrl: './marketplace-detail.component.css',
})
export class MarketplaceDetailComponent implements OnInit {
  readonly equipment = signal<EquipmentDTO | null>(null);
  readonly loading = signal(false);
  readonly bookingLoading = signal(false);
  readonly bookingSuccess = signal(false);
  readonly bookingError = signal<string | null>(null);
  readonly existingBookings = signal<BookingDTO[]>([]);

  startDate = '';
  endDate = '';

  readonly today = new Date().toISOString().slice(0, 10);

  readonly isOwner = computed(() => {
    const e = this.equipment();
    const u = this.auth.currentUser();
    return !!e && !!u && e.ownerId === u.userId;
  });

  readonly dateInvalid = computed(() => {
    if (!this.startDate || !this.endDate) return false;
    return this.endDate < this.startDate;
  });

  readonly bookingDays = computed(() => {
    if (!this.startDate || !this.endDate) return 0;
    const start = new Date(this.startDate);
    const end = new Date(this.endDate);
    return Math.max(1, Math.floor((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1);
  });

  readonly estimatedCost = computed(() => {
    const e = this.equipment();
    if (!e || !this.bookingDays()) return null;
    return Number(e.dailyRate) * this.bookingDays();
  });

  readonly dateOverlap = computed(() => {
    if (!this.startDate || !this.endDate) return false;
    const s = new Date(this.startDate);
    const e = new Date(this.endDate);
    return this.existingBookings().some((b) => {
      const bs = new Date(b.startDate);
      const be = new Date(b.endDate);
      return s <= be && e >= bs;
    });
  });

  readonly bookedRanges = computed(() => {
    return this.existingBookings()
      .filter((b) => b.status === 'CONFIRMED' || b.status === 'REQUESTED')
      .map((b) => ({ start: b.startDate, end: b.endDate }));
  });

  constructor(
    private route: ActivatedRoute,
    public svc: EquipmentService,
    private bookingSvc: BookingService,
    public auth: AuthService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.loading.set(true);
    this.svc.getById(id).subscribe({
      next: (e) => {
        this.equipment.set(e);
        this.loading.set(false);
        this.loadBookings(id);
      },
      error: () => this.loading.set(false),
    });
  }

  loadBookings(equipmentId: string): void {
    this.bookingSvc.getByEquipment(equipmentId).subscribe({
      next: (list) => this.existingBookings.set(list),
    });
  }

  book(): void {
    this.bookingError.set(null);
    if (!this.startDate || !this.endDate || this.dateInvalid()) return;

    const user = this.auth.currentUser();
    if (!user || !this.equipment()) return;

    this.bookingLoading.set(true);
    this.bookingSvc.create({
      equipmentId: this.equipment()!.equipmentId!,
      renterId: user.userId!,
      startDate: this.startDate,
      endDate: this.endDate,
    }).subscribe({
      next: () => {
        this.bookingLoading.set(false);
        this.bookingSuccess.set(true);
        this.loadBookings(this.equipment()!.equipmentId!);
      },
      error: (err: HttpErrorResponse) => {
        this.bookingLoading.set(false);
        const msg = err.error?.message || err.error?.error || '';
        this.bookingError.set(msg || 'Could not submit booking. Please try again.');
      },
    });
  }
}
