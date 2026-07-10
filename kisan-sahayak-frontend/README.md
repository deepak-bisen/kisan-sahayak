# Kisan Sahayak — Frontend (Angular)

Angular 17 standalone-components frontend for the Kisan Sahayak backend (Spring Boot microservices).
This first pass covers **authentication (register/login)** and an **interactive home page**, wired to the real
`kisan-user` API through the `api-gateway`.

## What's included

- `HomeComponent` — public landing page with a Farmer / Equipment-owner toggle, a scrolling
  "noticeboard" ticker, feature cards, and a how-it-works section.
- `RegisterComponent` — matches `UserDTO` exactly (fullName, phoneNumber, password, villageName,
  district, state, role).
- `LoginComponent` — matches `LoginRequestDTO` / `AuthResponseDTO`.
- `AuthService` — calls `POST /api/users/register` and `POST /api/users/login`, stores the JWT +
  user in `localStorage`, exposes `currentUser` / `isLoggedIn` as signals.
- `authInterceptor` — attaches `Authorization: Bearer <token>` to every outgoing request.
- `authGuard` — protects `/dashboard`, redirects to `/login` if not authenticated.
- `DashboardComponent` — minimal guarded landing page after login, to prove the auth loop end-to-end.
  (Equipment/marketplace/knowledge screens are intentionally left for the next pass.)

## Design direction

Palette and type are deliberately **not** generic SaaS green/teal — they're pulled from Indian
harvest-festival colour (marigold gold, dusk indigo, khadi cream, sage leaf) with a Fraunces +
Work Sans pairing. The signature element is the scrolling **"mandi noticeboard" ticker** on the home
page, styled like a hand-painted village signboard, echoed as a thin gold rule on the auth cards.

## Backend contract this assumes

| Frontend action | Backend endpoint | Method |
|---|---|---|
| Register | `/api/users/register` | `POST` (via gateway → `kisan-user`) |
| Login | `/api/users/login` | `POST` (via gateway → `kisan-user`) |

Base URL is set in `src/environments/environment.ts`:
```ts
apiUrl: 'http://localhost:8080/api'
```
Change this if your `api-gateway` runs on a different host/port. The gateway already has
CORS enabled for all origins (`spring.cloud.gateway.globalcors...allowedOrigins=*`), so no backend
changes should be needed to talk to `ng serve` on `http://localhost:4200`.

## Running it

```bash
npm install
npm start        # ng serve, defaults to http://localhost:4200
```

Make sure the backend is running first, in this order: `discovery-server` → `kisan-user` →
`api-gateway`. Then:

1. Open `http://localhost:4200` → interactive home page.
2. Click **Register** → fill the form → redirects to **Login** on success.
3. Log in → redirected to a guarded **/dashboard** showing your JWT-authenticated profile.
4. **Log out** clears the token and returns you to the home page.

## Known gaps (by design, for this pass)

- No equipment listing/booking or crop-guide UI yet — dashboard has placeholders for both.
- No refresh-token handling; if the JWT expires, the user needs to log in again.
- Backend's `deleteUserByPhone` endpoint is commented out server-side, so there's intentionally
  no "delete account" action in this UI yet.
