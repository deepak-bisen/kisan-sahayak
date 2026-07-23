import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { KnowledgeService } from '../../../core/services/knowledge.service';
import { CropGuideDTO } from '../../../core/models/crop-guide.model';
import { BreadcrumbComponent } from '../../../shared/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-knowledge-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, BreadcrumbComponent],
  templateUrl: './knowledge-list.component.html',
})
export class KnowledgeListComponent implements OnInit {
  readonly guides = signal<CropGuideDTO[]>([]);
  readonly loading = signal(false);
  readonly loadError = signal<string | null>(null);
  readonly searchQuery = signal('');
  readonly selectedSeason = signal('');

  readonly seasons = ['Rabi', 'Kharif', 'Zaid'];

  constructor(public svc: KnowledgeService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.loadError.set(null);
    const query = this.searchQuery().trim();
    const season = this.selectedSeason();

    const onError = () => { this.loading.set(false); this.loadError.set('Could not load crop guides.'); };
    const onNext = (r: CropGuideDTO[]) => { this.guides.set(r); this.loading.set(false); };

    if (season) {
      this.svc.searchBySeason(season).subscribe({ next: onNext, error: onError });
    } else if (query) {
      this.svc.searchByCrop(query).subscribe({ next: onNext, error: onError });
    } else {
      this.svc.getAll().subscribe({ next: onNext, error: onError });
    }
  }
}
