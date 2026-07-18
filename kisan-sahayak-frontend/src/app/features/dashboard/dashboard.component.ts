import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { BookingService } from '../../core/services/booking.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  readonly listingsCount = signal(0);
  readonly myBookingsCount = signal(0);
  readonly ownerRequestsCount = signal(0);
  readonly loading = signal(false);

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
      this.listingsCount.set(listings.length);
      this.myBookingsCount.set(renterBookings.length);

      if (listings.length) {
        forkJoin(
          listings.map((eq) =>
            eq.equipmentId
              ? this.bookingSvc.getByEquipment(eq.equipmentId).pipe(catchError(() => of([])))
              : of([])
          ),
        ).subscribe((results) => {
          this.ownerRequestsCount.set(results.reduce((sum, b) => sum + b.length, 0));
          this.loading.set(false);
        });
      } else {
        this.loading.set(false);
      }
    });
  }
}
