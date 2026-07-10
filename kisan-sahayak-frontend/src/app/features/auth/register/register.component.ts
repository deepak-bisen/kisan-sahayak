import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: '../auth-shared.css',
})
export class RegisterComponent {
  readonly loading = signal(false);
  readonly serverError = signal<string | null>(null);
  readonly success = signal(false);

  readonly roles: { value: UserRole; label: string }[] = [
    { value: 'FARMER', label: 'Farmer' },
    { value: 'EQUIPMENT_OWNER', label: 'Equipment owner' },
  ];

  form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    villageName: ['', [Validators.required]],
    district: ['', [Validators.required]],
    state: ['', [Validators.required]],
    role: ['FARMER' as UserRole, [Validators.required]],
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  get f() {
    return this.form.controls;
  }

  submit(): void {
    this.serverError.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    const v = this.form.value;

    this.auth
      .register({
        fullName: v.fullName!,
        phoneNumber: v.phoneNumber!,
        password: v.password!,
        villageName: v.villageName!,
        district: v.district!,
        state: v.state!,
        role: v.role as UserRole,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.success.set(true);
          setTimeout(() => this.router.navigate(['/login']), 1400);
        },
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          this.serverError.set(
            err.status === 409
              ? 'An account with this phone number already exists.'
              : err.status === 400
              ? 'Please check the highlighted fields and try again.'
              : 'Could not create your account right now. Please try again.'
          );
        },
      });
  }
}
