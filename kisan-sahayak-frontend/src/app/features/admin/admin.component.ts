import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { KnowledgeService } from '../../core/services/knowledge.service';
import { EquipmentService } from '../../core/services/equipment.service';
import { UserDTO } from '../../core/models/user.model';
import { CropGuideDTO } from '../../core/models/crop-guide.model';
import { EquipmentDTO } from '../../core/models/equipment.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

type AdminTab = 'users' | 'guides' | 'equipment';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin container">
      <p class="eyebrow">Admin Panel</p>
      <h1>Administration</h1>

      <div class="admin-tabs">
        <button class="admin-tab" [class.active]="activeTab() === 'users'" (click)="activeTab.set('users'); loadUsers()">Users</button>
        <button class="admin-tab" [class.active]="activeTab() === 'guides'" (click)="activeTab.set('guides'); loadGuides()">Crop Guides</button>
        <button class="admin-tab" [class.active]="activeTab() === 'equipment'" (click)="activeTab.set('equipment'); loadEquipment()">Equipment</button>
      </div>

      <!-- Users Tab -->
      @if (activeTab() === 'users') {
        <div class="admin-section">
          <h2>All Users ({{ users().length }})</h2>
          @if (usersLoading()) {
            <div class="skeleton-card"><div class="skeleton-line"></div></div>
          } @else {
            <table class="admin-table">
              <thead><tr><th>Name</th><th>Phone</th><th>Village</th><th>Roles</th><th></th></tr></thead>
              <tbody>
                @for (u of users(); track u.userId) {
                  <tr>
                    <td>{{ u.fullName }}</td>
                    <td>{{ u.phoneNumber }}</td>
                    <td>{{ u.villageName }}</td>
                    <td>{{ (u.roles || []).join(', ') }}</td>
                    <td>
                      <button class="btn btn-ghost btn-sm" (click)="deleteUser(u)" [disabled]="deletingUserId() === u.userId">
                        {{ deletingUserId() === u.userId ? 'Deleting…' : 'Delete' }}
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>
      }

      <!-- Guides Tab -->
      @if (activeTab() === 'guides') {
        <div class="admin-section">
          <h2>Crop Guides ({{ guides().length }})</h2>

          @if (editingGuide(); as g) {
            <div class="admin-form">
              <h3>{{ g.guideId ? 'Edit' : 'Add' }} Crop Guide</h3>
              <div class="auth-card__row">
                <div class="field"><label>Crop Name</label><input [(ngModel)]="g.cropName" /></div>
                <div class="field"><label>Season</label>
                  <select [(ngModel)]="g.season">
                    <option value="">Select</option><option value="Rabi">Rabi</option><option value="Kharif">Kharif</option><option value="Zaid">Zaid</option>
                  </select>
                </div>
              </div>
              <div class="auth-card__row">
                <div class="field"><label>Soil Type</label><input [(ngModel)]="g.soilType" /></div>
                <div class="field"><label>Duration (days)</label><input type="number" [(ngModel)]="g.durationInDays" /></div>
              </div>
              <div class="field"><label>Best Practices</label><textarea [(ngModel)]="g.bestPractices" rows="3"></textarea></div>
              <div class="field"><label>Disease Management</label><textarea [(ngModel)]="g.diseaseManagement" rows="2"></textarea></div>
              <div class="field"><label>Image URL</label><input [(ngModel)]="g.imageUrl" placeholder="https://…" /></div>
              <div class="field"><label>Video URL</label><input [(ngModel)]="g.videoUrl" placeholder="https://…" /></div>
              <div class="admin-form__actions">
                <button class="btn btn-ghost" (click)="cancelEditGuide()">Cancel</button>
                <button class="btn btn-primary" (click)="saveGuide()">{{ guideSaving() ? 'Saving…' : 'Save' }}</button>
              </div>
            </div>
          }

          @if (guidesLoading()) {
            <div class="skeleton-card"><div class="skeleton-line"></div></div>
          } @else {
            <button class="btn btn-primary" style="margin-bottom:16px" (click)="addGuide()">+ Add Guide</button>
            <table class="admin-table">
              <thead><tr><th>Crop</th><th>Season</th><th>Soil</th><th>Days</th><th></th></tr></thead>
              <tbody>
                @for (g of guides(); track g.guideId) {
                  <tr>
                    <td>{{ g.cropName }}</td>
                    <td>{{ g.season }}</td>
                    <td>{{ g.soilType }}</td>
                    <td>{{ g.durationInDays }}</td>
                    <td>
                      <button class="btn btn-ghost btn-sm" (click)="editGuide(g)">Edit</button>
                      <button class="btn btn-ghost btn-sm" (click)="deleteGuide(g)" [disabled]="deletingGuideId() === g.guideId">
                        {{ deletingGuideId() === g.guideId ? '…' : 'Delete' }}
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>
      }

      <!-- Equipment Tab -->
      @if (activeTab() === 'equipment') {
        <div class="admin-section">
          <h2>All Equipment ({{ equipmentList().length }})</h2>
          @if (eqLoading()) {
            <div class="skeleton-card"><div class="skeleton-line"></div></div>
          } @else {
            <table class="admin-table">
              <thead><tr><th>Name</th><th>Owner</th><th>Category</th><th>Daily Rate</th><th>Status</th><th></th></tr></thead>
              <tbody>
                @for (e of equipmentList(); track e.equipmentId) {
                  <tr>
                    <td>{{ e.name }}</td>
                    <td>{{ e.ownerName || e.ownerId }}</td>
                    <td>{{ e.category }}</td>
                    <td>₹{{ e.dailyRate }}</td>
                    <td>{{ e.isAvailable ? 'Available' : 'Unavailable' }}</td>
                    <td>
                      <button class="btn btn-ghost btn-sm" (click)="deleteEquipment(e)" [disabled]="deletingEqId() === e.equipmentId">
                        {{ deletingEqId() === e.equipmentId ? '…' : 'Delete' }}
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>
      }
    </section>
  `,
  styles: [`
    .admin-tabs { display:flex; gap:8px; margin:24px 0; border-bottom:1px solid var(--color-border); padding-bottom:0; }
    .admin-tab { padding:10px 20px; border:none; background:transparent; cursor:pointer; font-size:0.95rem; color:var(--color-cream-dim); border-bottom:2px solid transparent; transition:all 0.15s; }
    .admin-tab.active { color:var(--color-gold); border-bottom-color:var(--color-gold); font-weight:600; }
    .admin-section { margin-top:16px; }
    .admin-section h2 { font-size:1.1rem; margin-bottom:16px; }
    .admin-table { width:100%; border-collapse:collapse; font-size:0.9rem; }
    .admin-table th, .admin-table td { text-align:left; padding:10px 12px; border-bottom:1px solid var(--color-border); }
    .admin-table th { font-size:0.8rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--color-cream-dim); font-weight:600; }
    .admin-table td { color:var(--color-text); }
    .admin-form { background:var(--color-card-bg); border:1px solid var(--color-border); border-radius:var(--radius-md); padding:20px; margin-bottom:20px; }
    .admin-form h3 { margin-bottom:16px; font-size:1.05rem; }
    .admin-form__actions { display:flex; gap:12px; justify-content:flex-end; margin-top:16px; }
    .btn-sm { font-size:0.8rem; padding:4px 10px; }
  `],
})
export class AdminComponent implements OnInit {
  readonly activeTab = signal<AdminTab>('users');

  readonly users = signal<UserDTO[]>([]);
  readonly usersLoading = signal(false);
  readonly deletingUserId = signal<string | null>(null);

  readonly guides = signal<CropGuideDTO[]>([]);
  readonly guidesLoading = signal(false);
  readonly editingGuide = signal<CropGuideDTO | null>(null);
  readonly guideSaving = signal(false);
  readonly deletingGuideId = signal<string | null>(null);

  readonly equipmentList = signal<EquipmentDTO[]>([]);
  readonly eqLoading = signal(false);
  readonly deletingEqId = signal<string | null>(null);

  constructor(
    public auth: AuthService,
    private knowledgeSvc: KnowledgeService,
    private equipmentSvc: EquipmentService,
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  // --- Users ---
  loadUsers(): void {
    this.usersLoading.set(true);
    this.auth.getAllUsers().subscribe({ next: (r) => { this.users.set(r); this.usersLoading.set(false); }, error: () => this.usersLoading.set(false) });
  }

  deleteUser(u: UserDTO): void {
    if (!confirm(`Delete user "${u.fullName}"?`)) return;
    this.deletingUserId.set(u.userId!);
    this.auth.deleteUser(u.userId!).subscribe({ next: () => { this.users.update((list) => list.filter((x) => x.userId !== u.userId)); this.deletingUserId.set(null); }, error: () => this.deletingUserId.set(null) });
  }

  // --- Guides ---
  loadGuides(): void {
    this.guidesLoading.set(true);
    this.knowledgeSvc.getAll().subscribe({ next: (r) => { this.guides.set(r); this.guidesLoading.set(false); }, error: () => this.guidesLoading.set(false) });
  }

  addGuide(): void {
    this.editingGuide.set({ cropName: '', season: '', soilType: '', durationInDays: 30, bestPractices: '', diseaseManagement: '', imageUrl: '', videoUrl: '' });
  }

  editGuide(g: CropGuideDTO): void {
    this.editingGuide.set({ ...g });
  }

  cancelEditGuide(): void {
    this.editingGuide.set(null);
  }

  saveGuide(): void {
    const g = this.editingGuide();
    if (!g) return;
    this.guideSaving.set(true);

    if (g.guideId) {
      this.knowledgeSvc.update(g.guideId, g).subscribe({ next: () => { this.guideSaving.set(false); this.editingGuide.set(null); this.loadGuides(); }, error: () => this.guideSaving.set(false) });
    } else {
      this.knowledgeSvc.create(g).subscribe({ next: () => { this.guideSaving.set(false); this.editingGuide.set(null); this.loadGuides(); }, error: () => this.guideSaving.set(false) });
    }
  }

  deleteGuide(g: CropGuideDTO): void {
    if (!confirm(`Delete guide for "${g.cropName}"?`)) return;
    this.deletingGuideId.set(g.guideId!);
    this.knowledgeSvc.delete(g.guideId!).subscribe({ next: () => { this.guides.update((list) => list.filter((x) => x.guideId !== g.guideId)); this.deletingGuideId.set(null); }, error: () => this.deletingGuideId.set(null) });
  }

  // --- Equipment ---
  loadEquipment(): void {
    this.eqLoading.set(true);
    this.equipmentSvc.getAll().subscribe({ next: (r) => { this.equipmentList.set(r); this.eqLoading.set(false); }, error: () => this.eqLoading.set(false) });
  }

  deleteEquipment(e: EquipmentDTO): void {
    if (!confirm(`Delete equipment "${e.name}"?`)) return;
    this.deletingEqId.set(e.equipmentId!);
    this.equipmentSvc.delete(e.equipmentId!).subscribe({ next: () => { this.equipmentList.update((list) => list.filter((x) => x.equipmentId !== e.equipmentId)); this.deletingEqId.set(null); }, error: () => this.deletingEqId.set(null) });
  }
}
