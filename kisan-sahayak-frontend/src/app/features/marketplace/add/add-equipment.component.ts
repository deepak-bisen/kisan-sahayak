import { Component, OnInit, signal, OnDestroy } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { EquipmentService } from '../../../core/services/equipment.service';
import { AuthService } from '../../../core/services/auth.service';

const MAX_IMAGE_SIZE = 5 * 1024 * 1024;
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp'];

@Component({
  selector: 'app-add-equipment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-equipment.component.html',
})
export class AddEquipmentComponent implements OnInit, OnDestroy {
  readonly editingId = signal<string | null>(null);
  readonly loading = signal(false);
  readonly selectedFile = signal<File | null>(null);
  readonly previewUrl = signal<string | null>(null);
  readonly imageError = signal<string | null>(null);

  form = this.fb.group({
    name: ['', [Validators.required]],
    description: [''],
    category: ['', [Validators.required]],
    hourlyRate: [null as number | null, [Validators.required]],
    dailyRate: [null as number | null, [Validators.required]],
  });

  get f() { return this.form.controls; }

  constructor(
    private fb: FormBuilder,
    private svc: EquipmentService,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editingId.set(id);
      this.loading.set(true);
      this.svc.getById(id).subscribe({
        next: (e) => {
          this.form.patchValue({
            name: e.name,
            description: e.description || '',
            category: e.category,
            hourlyRate: Number(e.hourlyRate),
            dailyRate: Number(e.dailyRate),
          });
          if (e.imageUrl) this.previewUrl.set(this.svc.resolveImageUrl(e.imageUrl));
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
    }
  }

  ngOnDestroy(): void {
    const url = this.previewUrl();
    if (url?.startsWith('blob:')) URL.revokeObjectURL(url);
  }

  onFileSelected(event: Event): void {
    this.imageError.set(null);
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    if (file) {
      if (!ALLOWED_TYPES.includes(file.type)) {
        this.imageError.set('Only JPEG, PNG, and WebP images are allowed.');
        input.value = '';
        return;
      }
      if (file.size > MAX_IMAGE_SIZE) {
        this.imageError.set('Image must be less than 5 MB.');
        input.value = '';
        return;
      }
    }

    this.selectedFile.set(file);

    const old = this.previewUrl();
    if (old?.startsWith('blob:')) URL.revokeObjectURL(old);

    this.previewUrl.set(file ? URL.createObjectURL(file) : null);
  }

  clearImage(): void {
    this.imageError.set(null);
    this.selectedFile.set(null);
    const old = this.previewUrl();
    if (old?.startsWith('blob:')) URL.revokeObjectURL(old);
    this.previewUrl.set(null);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.auth.currentUser();
    if (!user) return;

    const fv = this.form.getRawValue();
    const payload = {
      name: fv.name!,
      description: fv.description || '',
      category: fv.category!,
      hourlyRate: Number(fv.hourlyRate),
      dailyRate: Number(fv.dailyRate),
      ownerId: user.userId!,
    };

    const id = this.editingId();

    const obs = id
      ? this.svc.update(id, payload)
      : this.svc.add(payload, this.selectedFile() ?? undefined);

    obs.subscribe({
      next: () => this.router.navigate(['/marketplace']),
      error: () => {},
    });
  }
}
