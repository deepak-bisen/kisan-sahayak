import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BookingDTO } from '../models/booking.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly base = `${environment.apiUrl}/marketplace/bookings`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    if (!token) return {};
    return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
  }

  create(b: BookingDTO): Observable<BookingDTO> {
    return this.http.post<BookingDTO>(this.base, b, this.authHeaders());
  }

  getById(id: string): Observable<BookingDTO> {
    return this.http.get<BookingDTO>(`${this.base}/${id}`, this.authHeaders());
  }

  getByRenter(renterId: string): Observable<BookingDTO[]> {
    return this.http.get<BookingDTO[]>(`${this.base}/renter/${renterId}`, this.authHeaders());
  }

  getByEquipment(equipmentId: string): Observable<BookingDTO[]> {
    return this.http.get<BookingDTO[]>(`${this.base}/equipment/${equipmentId}`, this.authHeaders());
  }

  updateStatus(id: string, status: string) {
    return this.http.patch<BookingDTO>(`${this.base}/${id}/status`, null, { params: new HttpParams().set('status', status), ...this.authHeaders() });
  }

  cancelByRenter(id: string, renterId: string) {
    return this.http.patch<BookingDTO>(`${this.base}/${id}/cancel`, null, { params: new HttpParams().set('renterId', renterId), ...this.authHeaders() });
  }
}
