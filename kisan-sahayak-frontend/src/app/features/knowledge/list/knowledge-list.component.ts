import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { KnowledgeService } from '../../../core/services/knowledge.service';
import { CropGuideDTO } from '../../../core/models/crop-guide.model';

@Component({
  selector: 'app-knowledge-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './knowledge-list.component.html',
})
export class KnowledgeListComponent implements OnInit {
  readonly guides = signal<CropGuideDTO[]>([]);
  readonly loading = signal(false);
  readonly searchQuery = signal('');
  readonly selectedSeason = signal('');

  readonly seasons = ['Rabi', 'Kharif', 'Zaid'];

  constructor(public svc: KnowledgeService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    const query = this.searchQuery().trim();
    const season = this.selectedSeason();

    if (season) {
      this.svc.searchBySeason(season).subscribe({ next: (r) => { this.guides.set(r); this.loading.set(false); }, error: (e) => { console.error('Failed to load guides:', e); this.loading.set(false); } });
    } else if (query) {
      this.svc.searchByCrop(query).subscribe({ next: (r) => { this.guides.set(r); this.loading.set(false); }, error: (e) => { console.error('Failed to search guides:', e); this.loading.set(false); } });
    } else {
      this.svc.getAll().subscribe({ next: (r) => { this.guides.set(r); this.loading.set(false); }, error: (e) => { console.error('Failed to load guides:', e); this.loading.set(false); } });
    }
  }
}
