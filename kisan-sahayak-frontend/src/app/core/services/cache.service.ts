import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

const MAX_ENTRIES = 100;

interface CacheEntry<T> {
  data: T;
  expiry: number;
  createdAt: number;
}

@Injectable({ providedIn: 'root' })
export class CacheService {
  private store = new Map<string, CacheEntry<unknown>>();

  get<T>(key: string): Observable<T> | null {
    const entry = this.store.get(key);
    if (!entry) return null;
    if (Date.now() > entry.expiry) {
      this.store.delete(key);
      return null;
    }
    return of(entry.data as T);
  }

  set<T>(key: string, data: T, ttlMs: number): void {
    if (this.store.size >= MAX_ENTRIES) {
      this.evictOldest();
    }
    this.store.set(key, { data, expiry: Date.now() + ttlMs, createdAt: Date.now() });
  }

  clear(): void {
    this.store.clear();
  }

  clearPrefix(prefix: string): void {
    for (const key of this.store.keys()) {
      if (key.startsWith(prefix)) this.store.delete(key);
    }
  }

  private evictOldest(): void {
    let oldestKey: string | null = null;
    let oldestTime = Infinity;
    for (const [key, entry] of this.store.entries()) {
      if (entry.createdAt < oldestTime) {
        oldestTime = entry.createdAt;
        oldestKey = key;
      }
    }
    if (oldestKey) this.store.delete(oldestKey);
  }
}
