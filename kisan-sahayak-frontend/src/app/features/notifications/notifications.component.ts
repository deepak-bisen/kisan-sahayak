import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { NotificationDTO } from '../../core/models/notification.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterLink, BreadcrumbComponent],
  template: `
    <div class="notifications-page container">
      <app-breadcrumb [items]="[{label:'Home',path:'/'},{label:'Notifications'}]" />
      <div class="notifications-page__head">
        <h1>Notifications</h1>
        @if (notifications().length > 0) {
          <button class="btn btn-ghost" (click)="markAllRead()">Mark all as read</button>
        }
      </div>

      @if (loading()) {
        <div class="skeleton-card" *ngFor="let _ of [1,2,3]">
          <div class="skeleton-line skeleton-line--h3"></div>
          <div class="skeleton-line skeleton-line--short"></div>
        </div>
      } @else if (loadError()) {
        <div class="empty-state">
          <p>{{ loadError() }}</p>
          <button class="btn btn-primary" (click)="ngOnInit()">Try again</button>
        </div>
      } @else if (notifications().length === 0) {
        <div class="empty-state">
          <p>No notifications yet.</p>
        </div>
      } @else {
        <div class="notifications-list">
          @for (n of notifications(); track n.id) {
            <div class="notification-item" [class.notification-item--unread]="!n.isRead">
              <div class="notification-item__dot" [class.notification-item__dot--read]="n.isRead"></div>
              <div class="notification-item__body">
                <p class="notification-item__msg">{{ n.message }}</p>
                <span class="notification-item__time">{{ n.createdAt | date:'medium' }}</span>
              </div>
              @if (!n.isRead) {
                <button class="btn btn-ghost btn-sm" (click)="markRead(n.id)">Dismiss</button>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .notifications-page { padding: 32px 16px; max-width: 720px; margin: 0 auto; }
    .notifications-page__head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
    .notifications-page__head h1 { margin: 0; }
    .notifications-list { display: flex; flex-direction: column; gap: 4px; }
    .notification-item { display: flex; align-items: flex-start; gap: 12px; padding: 16px; border-radius: var(--radius-md); border: 1px solid var(--color-border); background: var(--color-card-bg, #fff); }
    .notification-item--unread { border-left: 3px solid var(--color-gold); }
    .notification-item__dot { width: 8px; height: 8px; border-radius: 50%; background: var(--color-gold); margin-top: 6px; flex-shrink: 0; }
    .notification-item__dot--read { background: var(--color-border); }
    .notification-item__body { flex: 1; min-width: 0; }
    .notification-item__msg { margin: 0 0 4px; font-size: 0.95rem; }
    .notification-item__time { font-size: 0.8rem; color: var(--color-cream-dim); }
    .btn-sm { font-size: 0.8rem; padding: 4px 10px; }
  `],
})
export class NotificationsComponent implements OnInit {
  readonly notifications = signal<NotificationDTO[]>([]);
  readonly loading = signal(true);
  readonly loadError = signal<string | null>(null);
  private readonly base = `${environment.apiUrl}/marketplace/notifications`;

  constructor(
    private http: HttpClient,
    public auth: AuthService,
    private notifSvc: NotificationService,
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  private loadAll(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) { this.loading.set(false); return; }
    this.loadError.set(null);
    this.http.get<NotificationDTO[]>(`${this.base}/user/${user.userId}`).subscribe({
      next: (list) => { this.notifications.set(list); this.loading.set(false); },
      error: () => { this.loading.set(false); this.loadError.set('Could not load notifications.'); },
    });
  }

  markRead(id: string): void {
    this.notifSvc.markAsRead(id);
    this.notifications.update((list) => list.map((n) => n.id === id ? { ...n, isRead: true } : n));
  }

  markAllRead(): void {
    this.notifSvc.markAllAsRead();
    this.notifications.update((list) => list.map((n) => ({ ...n, isRead: true })));
  }
}
