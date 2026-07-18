import { Injectable, computed, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponseDTO, LoginRequestDTO, UserDTO } from '../models/user.model';

const TOKEN_KEY = 'ks_token';
const USER_KEY = 'ks_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/users`;

  // Holds the logged-in user (or null). Read this from components as a signal.
  readonly currentUser = signal<UserDTO | null>(this.readStoredUser());
  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  constructor(private http: HttpClient) {}

  register(payload: UserDTO): Observable<UserDTO> {
    return this.http.post<UserDTO>(`${this.baseUrl}/register`, payload);
  }

  login(payload: LoginRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.baseUrl}/login`, payload).pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        localStorage.setItem(USER_KEY, JSON.stringify(res.user));
        this.currentUser.set(res.user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  updateProfile(userId: string, payload: Partial<UserDTO>): Observable<UserDTO> {
    const token = this.getToken();
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
    return this.http.put<UserDTO>(`${this.baseUrl}/update/${userId}`, payload, { headers });
  }

  private readStoredUser(): UserDTO | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserDTO;
    } catch {
      return null;
    }
  }
}
