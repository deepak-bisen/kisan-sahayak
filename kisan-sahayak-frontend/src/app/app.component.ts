import { Component, signal, HostListener } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { NotificationService } from './core/services/notification.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastComponent } from './shared/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, ToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  readonly menuOpen = signal(false);

  constructor(
    public auth: AuthService,
    public notif: NotificationService,
    private router: Router,
  ) {}

  toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
    this.notif.closeDropdown();
  }

  logout(): void {
    this.closeMenu();
    this.notif.closeDropdown();
    this.auth.logout();
    this.router.navigate(['/']);
  }

  @HostListener('document:keydown.escape')
  handleEscape(): void {
    this.closeMenu();
    this.notif.closeDropdown();
  }
}
