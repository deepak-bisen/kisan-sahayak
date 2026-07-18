import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CropGuideDTO } from '../models/crop-guide.model';

@Injectable({ providedIn: 'root' })
export class KnowledgeService {
  private readonly base = `${environment.apiUrl}/knowledge/guides`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<CropGuideDTO[]> {
    return this.http.get<CropGuideDTO[]>(`${this.base}/all`);
  }

  getById(id: string): Observable<CropGuideDTO> {
    return this.http.get<CropGuideDTO>(`${this.base}/${id}`);
  }

  searchByCrop(cropName: string): Observable<CropGuideDTO[]> {
    return this.http.get<CropGuideDTO[]>(`${this.base}/search/crop/${encodeURIComponent(cropName)}`);
  }

  searchBySeason(season: string): Observable<CropGuideDTO[]> {
    return this.http.get<CropGuideDTO[]>(`${this.base}/search/season/${encodeURIComponent(season)}`);
  }

  create(guide: CropGuideDTO): Observable<CropGuideDTO> {
    return this.http.post<CropGuideDTO>(this.base, guide);
  }

  update(id: string, guide: CropGuideDTO): Observable<CropGuideDTO> {
    return this.http.put<CropGuideDTO>(`${this.base}/${id}`, guide);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
