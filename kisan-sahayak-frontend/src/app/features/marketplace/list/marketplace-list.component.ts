import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { EquipmentService } from '../../../core/services/equipment.service';
import { AuthService } from '../../../core/services/auth.service';
import { EquipmentDTO } from '../../../core/models/equipment.model';

@Component({
  selector: 'app-marketplace-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './marketplace-list.component.html',
})
export class MarketplaceListComponent implements OnInit {
  readonly equipments = signal<EquipmentDTO[]>([]);
  readonly loading = signal(false);

  constructor(private svc: EquipmentService, private router: Router, public auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe({
      next: (r) => {
        this.equipments.set(r || []);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
