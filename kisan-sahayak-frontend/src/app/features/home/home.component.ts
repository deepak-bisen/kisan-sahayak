import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

type Audience = 'FARMER' | 'EQUIPMENT_OWNER';

interface AudienceCopy {
  eyebrow: string;
  headline: string;
  sub: string;
  cta: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  readonly audience = signal<Audience>('FARMER');

  private readonly copy: Record<Audience, AudienceCopy> = {
    FARMER: {
      eyebrow: 'मैं किसान हूँ · For farmers',
      headline: 'Rent the tractor down the road, not the one three villages over.',
      sub: 'Find tillers, tractors and harvesters listed by owners near your village, book them for the days you actually need, and read crop guides written for your soil and season.',
      cta: 'Find equipment near me',
    },
    EQUIPMENT_OWNER: {
      eyebrow: 'मेरे पास उपकरण है · For equipment owners',
      headline: 'Your machine sits idle half the season. Someone nearby needs it this week.',
      sub: 'List your tractor, thresher or plough once. Set your village and rate, and farmers nearby can find and book it directly — no middleman taking a cut.',
      cta: 'List your equipment',
    },
  };

  readonly active = computed(() => this.copy[this.audience()]);

  readonly tickerItems = [
    'तवा तिलर उपलब्ध — Sehore, MP',
    'नई गाइड: Rabi season wheat sowing depth',
    'हार्वेस्टर बुक करें — Vidisha block',
    'गाइड अपडेट: Soil moisture before ploughing',
    'ट्रैक्टर + ट्रॉली — Raisen, 3 din ke liye',
  ];

  setAudience(a: Audience): void {
    this.audience.set(a);
  }
}
