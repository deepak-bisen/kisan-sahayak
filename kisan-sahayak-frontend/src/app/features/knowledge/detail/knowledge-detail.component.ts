import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { KnowledgeService } from '../../../core/services/knowledge.service';
import { CropGuideDTO } from '../../../core/models/crop-guide.model';

@Component({
  selector: 'app-knowledge-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="knowledge-detail container">
      <a routerLink="/knowledge" class="btn btn-ghost" style="margin-bottom:16px">&larr; Back to Knowledge Hub</a>

      @if (loading()) {
        <div class="skeleton-card">
          <div class="skeleton-line skeleton-line--h3"></div>
          <div class="skeleton-line skeleton-line--short"></div>
          <div class="skeleton-line"></div>
          <div class="skeleton-line"></div>
        </div>
      } @else if (error()) {
        <div class="empty-state">
          <p>{{ error() }}</p>
          <a routerLink="/knowledge" class="btn btn-primary">Browse guides</a>
        </div>
      } @else {
        @if (guide(); as g) {
          <div class="guide-card guide-card--detail">
            @if (g.imageUrl) {
              <div class="guide-media">
                <img [src]="svc.resolveUrl(g.imageUrl)" alt="{{ g.cropName }}" />
              </div>
            }
            <h1>{{ g.cropName }}</h1>
            <p class="meta">{{ g.season }} &middot; {{ g.soilType }} &middot; {{ g.durationInDays }} days</p>

            <div class="guide-section">
              <h2>Best Practices</h2>
              <p>{{ g.bestPractices }}</p>
            </div>

            @if (g.diseaseManagement) {
              <div class="guide-section">
                <h2>Disease Management</h2>
                <p>{{ g.diseaseManagement }}</p>
              </div>
            }

            @if (g.videoUrl) {
              <div class="guide-section">
                <h2>Video Guide</h2>
                <div class="guide-video">
                  <video [src]="svc.resolveUrl(g.videoUrl)" controls class="guide-video__player"></video>
                </div>
              </div>
            }
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .knowledge-detail { padding: 32px 16px; }
    .guide-card--detail { max-width: 800px; margin: 0 auto; }
    .guide-card--detail h1 { font-size: 1.8rem; margin: 0 0 8px; }
    .guide-card--detail .meta { margin: 0 0 24px; }
    .guide-section { margin: 24px 0; }
    .guide-section h2 { font-size: 1.2rem; margin: 0 0 8px; }
  `],
})
export class KnowledgeDetailComponent implements OnInit {
  readonly guide = signal<CropGuideDTO | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  constructor(
    private route: ActivatedRoute,
    public svc: KnowledgeService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error.set('No guide ID provided.');
      this.loading.set(false);
      return;
    }
    this.svc.getById(id).subscribe({
      next: (g) => { this.guide.set(g); this.loading.set(false); },
      error: () => { this.error.set('Failed to load crop guide.'); this.loading.set(false); },
    });
  }
}
