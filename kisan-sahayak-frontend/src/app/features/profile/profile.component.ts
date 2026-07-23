import { Component, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, BreadcrumbComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  readonly loading = signal(false);
  readonly success = signal<string | null>(null);
  readonly serverError = signal<string | null>(null);
  readonly confirmDelete = signal(false);
  readonly deleting = signal(false);

  form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3)]],
    villageName: ['', [Validators.required]],
    district: ['', [Validators.required]],
    state: ['', [Validators.required]],
    password: [''],
  });

  get f() { return this.form.controls; }

  constructor(
    private fb: FormBuilder,
    public auth: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (!user) return;
    this.form.patchValue({
      fullName: user.fullName,
      villageName: user.villageName,
      district: user.district,
      state: user.state,
    });
  }

  submit(): void {
    this.serverError.set(null);
    this.success.set(null);
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const user = this.auth.currentUser();
    if (!user?.userId) return;

    this.loading.set(true);
    const v = this.form.value;

    this.auth.updateProfile(user.userId, {
      fullName: v.fullName!,
      villageName: v.villageName!,
      district: v.district!,
      state: v.state!,
      password: v.password || undefined,
    }).subscribe({
      next: (updated) => {
        this.loading.set(false);
        this.success.set('Profile updated successfully.');
        this.auth.currentUser.set(updated);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.serverError.set(err.error?.message || 'Failed to update profile.');
      },
    });
  }

  deleteAccount(): void {
    const user = this.auth.currentUser();
    if (!user?.userId) return;
    this.deleting.set(true);
    this.auth.deleteUser(user.userId).subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/']);
      },
      error: () => {
        this.deleting.set(false);
        this.confirmDelete.set(false);
        this.serverError.set('Failed to delete account. Please try again.');
      },
    });
  }
}
