import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EquipmentDTO } from '../models/equipment.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly base = `${environment.apiUrl}/marketplace/equipment`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    if (!token) return {};
    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  getAll(): Observable<EquipmentDTO[]> {
    return this.http.get<EquipmentDTO[]>(this.base);
  }

  getById(id: string): Observable<EquipmentDTO> {
    return this.http.get<EquipmentDTO>(`${this.base}/${id}`);
  }

  getByOwner(ownerId: string): Observable<EquipmentDTO[]> {
    return this.http.get<EquipmentDTO[]>(`${this.base}/owner/${ownerId}`);
  }

  add(e: EquipmentDTO): Observable<EquipmentDTO> {
    return this.http.post<EquipmentDTO>(this.base, e, this.authHeaders());
  }

  update(id: string, e: EquipmentDTO): Observable<EquipmentDTO> {
    return this.http.put<EquipmentDTO>(`${this.base}/${id}`, e, this.authHeaders());
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`, this.authHeaders());
  }

  searchByCategory(category: string) {
    return this.http.get<EquipmentDTO[]>(`${this.base}/search/category/${encodeURIComponent(category)}`);
  }

  searchByVillage(village: string) {
    return this.http.get<EquipmentDTO[]>(`${this.base}/search/village/${encodeURIComponent(village)}`);
  }
}
