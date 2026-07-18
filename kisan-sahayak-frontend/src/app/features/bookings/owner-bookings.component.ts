import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { BookingService } from '../../core/services/booking.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { AuthService } from '../../core/services/auth.service';
import { BookingDTO, BookingStatus } from '../../core/models/booking.model';
import { EquipmentDTO } from '../../core/models/equipment.model';

interface EquipmentWithBookings {
  equipment: EquipmentDTO;
  bookings: BookingDTO[];
}

@Component({
  selector: 'app-owner-bookings',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './owner-bookings.component.html',
  styleUrl: './owner-bookings.component.css',
})
export class OwnerBookingsComponent implements OnInit {
  readonly loading = signal(false);
  readonly updating = signal<string | null>(null);
  readonly items = signal<EquipmentWithBookings[]>([]);

  constructor(
    private equipmentSvc: EquipmentService,
    private bookingSvc: BookingService,
    public auth: AuthService,
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.load(user.userId);
  }

  statusColor(status?: BookingStatus): string {
    switch (status) {
      case 'REQUESTED': return 'var(--color-gold)';
      case 'CONFIRMED': return 'var(--color-leaf)';
      case 'COMPLETED': return 'var(--color-cream-dim)';
      case 'CANCELLED': return 'var(--color-clay)';
      default: return 'var(--color-cream-dim)';
    }
  }

  updateStatus(bookingId: string, status: BookingStatus): void {
    this.updating.set(bookingId);
    this.bookingSvc.updateStatus(bookingId, status).subscribe({
      next: () => {
        this.items.update((items) =>
          items.map((eq) => ({
            ...eq,
            bookings: eq.bookings.map((b) =>
              b.bookingId === bookingId ? { ...b, status } : b
            ),
          }))
        );
        this.updating.set(null);
      },
      error: () => this.updating.set(null),
    });
  }

  private load(ownerId: string): void {
    this.loading.set(true);
    this.equipmentSvc.getByOwner(ownerId).pipe(
      switchMap((equipmentList) => {
        if (!equipmentList.length) return of([]);
        return forkJoin(
          equipmentList.map((eq) =>
            this.bookingSvc.getByEquipment(eq.equipmentId!).pipe(
              catchError(() => of([])),
              switchMap((bookings) => of({ equipment: eq, bookings } as EquipmentWithBookings)),
            )
          ),
        );
      }),
      catchError(() => of([])),
    ).subscribe({
      next: (result) => { this.items.set(result); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
