import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EquipmentService } from '../../../core/services/equipment.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-add-equipment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-equipment.component.html',
})
export class AddEquipmentComponent {
  form = this.fb.group({
    name: ['', [Validators.required]],
    description: [''],
    category: ['', [Validators.required]],
    hourlyRate: [null, [Validators.required]],
    dailyRate: [null, [Validators.required]],
    imageUrl: [''],
  });

  constructor(private fb: FormBuilder, private svc: EquipmentService, private auth: AuthService, private router: Router) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.auth.currentUser();
    if (!user) return;

    const fv = this.form.value;
    const payload = {
      name: fv.name!,
      description: fv.description || '',
      category: fv.category!,
      hourlyRate: Number(fv.hourlyRate),
      dailyRate: Number(fv.dailyRate),
      imageUrl: fv.imageUrl || '',
      ownerId: user.userId!,
    };

    this.svc.add(payload).subscribe({ next: () => this.router.navigate(['/marketplace']), error: () => {} });
  }
}
