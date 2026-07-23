import { Component, OnInit, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { BookingService } from '../../core/services/booking.service';
import { BookingDTO } from '../../core/models/booking.model';
import { EquipmentDTO } from '../../core/models/equipment.model';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, BreadcrumbComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  readonly loading = signal(false);
  readonly listings = signal<EquipmentDTO[]>([]);
  readonly renterBookings = signal<BookingDTO[]>([]);
  readonly ownerBookings = signal<BookingDTO[]>([]);

  readonly listingsCount = computed(() => this.listings().length);
  readonly myBookingsCount = computed(() => this.renterBookings().length);
  readonly ownerRequestsCount = computed(() => this.ownerBookings().length);

  readonly totalEarnings = computed(() =>
    this.ownerBookings()
      .filter((b) => b.status === 'CONFIRMED' || b.status === 'COMPLETED')
      .reduce((sum, b) => sum + (b.totalCost || 0), 0),
  );

  readonly activeRentals = computed(() =>
    this.renterBookings().filter((b) => b.status === 'CONFIRMED').length,
  );

  readonly statusBreakdown = computed(() => {
    const all = this.renterBookings();
    return {
      REQUESTED: all.filter((b) => b.status === 'REQUESTED').length,
      CONFIRMED: all.filter((b) => b.status === 'CONFIRMED').length,
      COMPLETED: all.filter((b) => b.status === 'COMPLETED').length,
      CANCELLED: all.filter((b) => b.status === 'CANCELLED').length,
    };
  });

  readonly topEquipment = computed(() => {
    const bookings = this.ownerBookings();
    const equipment = this.listings();
    if (!bookings.length || !equipment.length) return null;

    const counts: Record<string, number> = {};
    for (const b of bookings) {
      counts[b.equipmentId] = (counts[b.equipmentId] || 0) + 1;
    }

    const topId = Object.entries(counts).sort((a, b) => b[1] - a[1])[0]?.[0];
    const topEq = equipment.find((e) => e.equipmentId === topId);
    return topEq ? { name: topEq.name, count: counts[topId!] } : null;
  });

  constructor(
    public auth: AuthService,
    private equipmentSvc: EquipmentService,
    private bookingSvc: BookingService,
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.loading.set(true);

    forkJoin({
      listings: this.equipmentSvc.getByOwner(user.userId).pipe(catchError(() => of([]))),
      renterBookings: this.bookingSvc.getByRenter(user.userId).pipe(catchError(() => of([]))),
    }).subscribe(({ listings, renterBookings }) => {
      this.listings.set(listings);
      this.renterBookings.set(renterBookings);

      if (listings.length) {
        forkJoin(
          listings.map((eq) =>
            eq.equipmentId
              ? this.bookingSvc.getByEquipment(eq.equipmentId).pipe(catchError(() => of([])))
              : of([])
          ),
        ).subscribe((results) => {
          const all = results.flat();
          const seen = new Set<string>();
          const unique = all.filter((b) => {
            const key = b.bookingId || `${b.equipmentId}-${b.renterId}-${b.startDate}`;
            if (seen.has(key)) return false;
            seen.add(key);
            return b.renterId !== user.userId;
          });
          this.ownerBookings.set(unique);
          this.loading.set(false);
        });
      } else {
        this.loading.set(false);
      }
    });
  }
}
