import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EquipmentDTO } from '../models/equipment.model';
import { AuthService } from './auth.service';
import { CacheService } from './cache.service';

const CACHE_TTL = 86_400_000; // 24 hours

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly base = `${environment.apiUrl}/marketplace/equipment`;
  private readonly cachePrefix = 'equipment_';

  constructor(private http: HttpClient, private auth: AuthService, private cache: CacheService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    if (!token) return {};
    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  getAll(): Observable<EquipmentDTO[]> {
    const cached = this.cache.get<EquipmentDTO[]>(`${this.cachePrefix}all`);
    if (cached) return cached;
    return this.http.get<EquipmentDTO[]>(this.base).pipe(
      tap((data) => this.cache.set(`${this.cachePrefix}all`, data, CACHE_TTL)),
    );
  }

  getById(id: string): Observable<EquipmentDTO> {
    return this.http.get<EquipmentDTO>(`${this.base}/${id}`);
  }

  getByOwner(ownerId: string): Observable<EquipmentDTO[]> {
    return this.http.get<EquipmentDTO[]>(`${this.base}/owner/${ownerId}`);
  }

  add(e: EquipmentDTO, image?: File): Observable<EquipmentDTO> {
    const formData = new FormData();
    formData.append('equipment', new Blob([JSON.stringify(e)], { type: 'application/json' }), 'equipment');

    if (image) {
      formData.append('image', image, image.name);
    } else {
      formData.append('image', new Blob([], { type: 'application/octet-stream' }), 'image');
    }

    return this.http.post<EquipmentDTO>(this.base, formData, this.authHeaders()).pipe(
      tap(() => this.cache.clearPrefix(this.cachePrefix)),
    );
  }

  update(id: string, e: EquipmentDTO): Observable<EquipmentDTO> {
    return this.http.put<EquipmentDTO>(`${this.base}/${id}`, e, this.authHeaders()).pipe(
      tap(() => this.cache.clearPrefix(this.cachePrefix)),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`, this.authHeaders()).pipe(
      tap(() => this.cache.clearPrefix(this.cachePrefix)),
    );
  }

  searchByCategory(category: string) {
    return this.http.get<EquipmentDTO[]>(`${this.base}/search/category/${encodeURIComponent(category)}`);
  }

  searchByVillage(village: string) {
    return this.http.get<EquipmentDTO[]>(`${this.base}/search/village/${encodeURIComponent(village)}`);
  }

  /** Resolve a relative image URL through the API gateway. */
  resolveImageUrl(url: string | null | undefined): string | null {
    if (!url) return null;
    if (url.startsWith('http')) return url;
    return `${environment.baseUrl}${url}`;
  }
}
