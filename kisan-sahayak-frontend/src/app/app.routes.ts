import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then((m) => m.HomeComponent),
    title: 'Kisan Sahayak — Rent equipment, grow better',
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent),
    title: 'Log in — Kisan Sahayak',
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
    title: 'Create account — Kisan Sahayak',
  },
  {
    path: 'knowledge',
    loadComponent: () =>
      import('./features/knowledge/list/knowledge-list.component').then((m) => m.KnowledgeListComponent),
    title: 'Knowledge Hub — Kisan Sahayak',
  },
  {
    path: 'knowledge/:id',
    loadComponent: () =>
      import('./features/knowledge/detail/knowledge-detail.component').then((m) => m.KnowledgeDetailComponent),
    title: 'Crop Guide — Kisan Sahayak',
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./features/profile/profile.component').then((m) => m.ProfileComponent),
    canActivate: [authGuard],
    title: 'Profile — Kisan Sahayak',
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
    canActivate: [authGuard],
    title: 'Dashboard — Kisan Sahayak',
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./features/notifications/notifications.component').then((m) => m.NotificationsComponent),
    canActivate: [authGuard],
    title: 'Notifications — Kisan Sahayak',
  },
  {
    path: 'marketplace',
    loadComponent: () => import('./features/marketplace/list/marketplace-list.component').then((m) => m.MarketplaceListComponent),
    title: 'Marketplace — Kisan Sahayak',
  },
  {
    path: 'marketplace/add',
    loadComponent: () => import('./features/marketplace/add/add-equipment.component').then((m) => m.AddEquipmentComponent),
    canActivate: [authGuard],
    title: 'List Equipment — Kisan Sahayak',
  },
  {
    path: 'marketplace/mine',
    loadComponent: () => import('./features/marketplace/owner/my-listings.component').then((m) => m.MyListingsComponent),
    canActivate: [authGuard],
    title: 'My Listings — Kisan Sahayak',
  },
  {
    path: 'marketplace/:id',
    loadComponent: () => import('./features/marketplace/detail/marketplace-detail.component').then((m) => m.MarketplaceDetailComponent),
    title: 'Equipment — Kisan Sahayak',
  },
  {
    path: 'marketplace/:id/edit',
    loadComponent: () => import('./features/marketplace/add/add-equipment.component').then((m) => m.AddEquipmentComponent),
    canActivate: [authGuard],
    title: 'Edit Equipment — Kisan Sahayak'
  },
  {
    path: 'bookings',
    loadComponent: () => import('./features/bookings/my-bookings.component').then((m) => m.MyBookingsComponent),
    canActivate: [authGuard],
    title: 'My Bookings — Kisan Sahayak',
  },
  {
    path: 'bookings/manage',
    loadComponent: () => import('./features/bookings/owner-bookings.component').then((m) => m.OwnerBookingsComponent),
    canActivate: [authGuard],
    title: 'Manage Bookings — Kisan Sahayak',
  },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin.component').then((m) => m.AdminComponent),
    canActivate: [authGuard, adminGuard],
    title: 'Admin Panel — Kisan Sahayak',
  },
  { path: '**', redirectTo: '' },
];
