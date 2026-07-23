import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { BookingService } from '../../core/services/booking.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { BookingDTO, BookingStatus } from '../../core/models/booking.model';
import { EquipmentDTO } from '../../core/models/equipment.model';
import { UserDTO } from '../../core/models/user.model';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';

interface BookingWithRenter {
  booking: BookingDTO;
  renterContact: UserDTO | null;
}

interface EquipmentWithBookings {
  equipment: EquipmentDTO;
  bookings: BookingWithRenter[];
}

@Component({
  selector: 'app-owner-bookings',
  standalone: true,
  imports: [CommonModule, RouterLink, BreadcrumbComponent],
  templateUrl: './owner-bookings.component.html',
  styleUrl: './owner-bookings.component.css',
})
export class OwnerBookingsComponent implements OnInit {
  readonly loading = signal(false);
  readonly loadError = signal<string | null>(null);
  readonly updating = signal<string | null>(null);
  readonly items = signal<EquipmentWithBookings[]>([]);

  constructor(
    public equipmentSvc: EquipmentService,
    private bookingSvc: BookingService,
    public auth: AuthService,
    private toast: ToastService,
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.load(user.userId);
  }

  bookingDays(b: BookingDTO): number {
    if (!b.startDate || !b.endDate) return 0;
    const start = new Date(b.startDate);
    const end = new Date(b.endDate);
    return Math.max(1, Math.floor((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1);
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
              b.booking.bookingId === bookingId ? { ...b, booking: { ...b.booking, status } } : b
            ),
          }))
        );
        this.updating.set(null);
        const label = status === 'CONFIRMED' ? 'confirmed' : status === 'COMPLETED' ? 'completed' : 'cancelled';
        this.toast.success(`Booking ${label}.`);
      },
      error: () => { this.updating.set(null); this.toast.error('Failed to update booking.'); },
    });
  }

  private load(ownerId: string): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.equipmentSvc.getByOwner(ownerId).pipe(
      switchMap((equipmentList) => {
        if (!equipmentList.length) return of([]);
        return forkJoin(
          equipmentList.map((eq) =>
            this.bookingSvc.getByEquipment(eq.equipmentId!).pipe(
              catchError(() => of([])),
              switchMap((bookings) => {
                const renterIds = [...new Set(bookings.map((b) => b.renterId))];
                if (!renterIds.length) return of({ equipment: eq, bookings: bookings.map((b) => ({ booking: b, renterContact: null } as BookingWithRenter)) } as EquipmentWithBookings);
                return forkJoin(
                  renterIds.map((id) => this.auth.getUserById(id).pipe(catchError(() => of(null))))
                ).pipe(
                  map((renterUsers) => {
                    const renterMap = new Map<string, UserDTO | null>();
                    renterIds.forEach((id, i) => renterMap.set(id, renterUsers[i]));
                    return {
                      equipment: eq,
                      bookings: bookings.map((b) => ({
                        booking: b,
                        renterContact: renterMap.get(b.renterId) ?? null,
                      } as BookingWithRenter)),
                    } as EquipmentWithBookings;
                  }),
                );
              }),
            )
          ),
        );
      }),
      catchError(() => of([])),
    ).subscribe({
      next: (result) => { this.items.set(result); this.loading.set(false); },
      error: () => { this.loading.set(false); this.loadError.set('Could not load bookings.'); },
    });
  }
}
