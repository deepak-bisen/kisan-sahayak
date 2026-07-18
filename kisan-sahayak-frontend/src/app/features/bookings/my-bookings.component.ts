import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../core/services/booking.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { AuthService } from '../../core/services/auth.service';
import { BookingDTO, BookingStatus } from '../../core/models/booking.model';
import { EquipmentDTO } from '../../core/models/equipment.model';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

interface BookingWithEquipment {
  booking: BookingDTO;
  equipment: EquipmentDTO | null;
}

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-bookings.component.html',
  styleUrl: './my-bookings.component.css',
})
export class MyBookingsComponent implements OnInit {
  readonly loading = signal(false);
  readonly bookings = signal<BookingWithEquipment[]>([]);

  constructor(
    private bookingSvc: BookingService,
    private equipmentSvc: EquipmentService,
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

  private load(renterId: string): void {
    this.loading.set(true);
    this.bookingSvc.getByRenter(renterId).pipe(
      switchMap((bookings) => {
        if (!bookings.length) return of([]);
        return forkJoin(
          bookings.map((b) =>
            this.equipmentSvc.getById(b.equipmentId).pipe(
              catchError(() => of(null)),
            ),
          ),
        ).pipe(
          switchMap((equipments) =>
            of(bookings.map((b, i) => ({ booking: b, equipment: equipments[i] } as BookingWithEquipment))),
          ),
        );
      }),
      catchError(() => of([])),
    ).subscribe({
      next: (result) => { this.bookings.set(result); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
