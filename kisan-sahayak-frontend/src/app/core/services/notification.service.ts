import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, interval, switchMap, startWith } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationDTO } from '../models/notification.model';
import { AuthService } from './auth.service';

const POLL_INTERVAL = 30_000; // 30 seconds

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly base = `${environment.apiUrl}/marketplace/notifications`;

  readonly unreadCount = signal(0);
  readonly notifications = signal<NotificationDTO[]>([]);
  readonly showDropdown = signal(false);

  constructor(private http: HttpClient, private auth: AuthService) {
    if (auth.isLoggedIn()) {
      interval(POLL_INTERVAL).pipe(startWith(0), switchMap(() => this.fetchUnreadCount())).subscribe({
        next: (c) => this.unreadCount.set(c),
      });
    }
  }

  private fetchUnreadCount(): Observable<number> {
    const user = this.auth.currentUser();
    if (!user?.userId) return new Observable((s) => { s.next(0); s.complete(); });
    return this.http.get<number>(`${this.base}/user/${user.userId}/unread/count`);
  }

  load(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.http.get<NotificationDTO[]>(`${this.base}/user/${user.userId}/unread`).subscribe({
      next: (list) => {
        this.notifications.set(list);
        this.unreadCount.set(list.length);
      },
    });
  }

  markAsRead(id: string): void {
    this.http.patch(`${this.base}/${id}/read`, {}).subscribe({
      next: () => {
        this.notifications.update((list) => list.filter((n) => n.id !== id));
        this.unreadCount.update((c) => Math.max(0, c - 1));
      },
    });
  }

  markAllAsRead(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.http.patch(`${this.base}/user/${user.userId}/read-all`, {}).subscribe({
      next: () => {
        this.notifications.set([]);
        this.unreadCount.set(0);
      },
    });
  }

  toggleDropdown(): void {
    this.showDropdown.update((v) => !v);
    if (this.showDropdown()) this.load();
  }

  closeDropdown(): void {
    this.showDropdown.set(false);
  }
}
