import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: '../auth-shared.css',
})
export class LoginComponent {
  readonly loading = signal(false);
  readonly serverError = signal<string | null>(null);

  form = this.fb.group({
    phoneNumber: ['', [Validators.required, Validators.pattern(/^[6-9][0-9]{9}$/)]],
    password: ['', [Validators.required]],
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
    this.auth
      .login({
        phoneNumber: this.form.value.phoneNumber!,
        password: this.form.value.password!,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/marketplace']);
        },
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          this.serverError.set(
            err.status === 401 || err.status === 404
              ? 'Phone number or password is incorrect.'
              : 'Could not log in right now. Please try again.'
          );
        },
      });
  }
}
