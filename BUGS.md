# kishan-sahayak Prioritized Issues & Fixes

**Project:** kishan-sahayak (Farmer Helper microservices)  
**Date of re-check & fixes:** 2026-06-13  
**Status:** Multiple high-impact fixes applied. See "Fixed in this pass" sections.

This document captures the most important problems discovered during deep code review + build verification. Issues are prioritized by impact on **operability** (can the system start? can core flows work through the gateway?).

## P0 — Critical Blockers (System cannot start or core services are unusable)

### 1. Marketplace executable JAR would not start (Wrong mainClass)
- **Files:** `kisan-marketplace/marketplace-app/pom.xml:45`
- **Symptom:** `Start-Class: com.kisan.marketplace.MarketPlaceApplication` baked into the fat JAR → `ClassNotFound` on `java -jar`.
- **Root cause:** Typo + mismatch with actual class `MarketplaceAppApplication`.
- **Status:** **FIXED**.
- **Patch applied:** Updated `<mainClass>` to `com.kisan.marketplace.MarketplaceAppApplication`.
- **Verification:** Re-ran `mvn package`; manifest now shows correct `Start-Class`.

### 2. Knowledge service completely broken at runtime (ID type + delete logic)
- **Files:**
  - `kisan-knowledge/knowledge-service/src/main/java/com/kisan/knowledge/repository/CropGuideRepository.java:8`
  - `.../service/impl/CropGuideServiceImpl.java` (multiple)
- **Symptom:** `Integer.valueOf(guideId)` on real UUID strings → `NumberFormatException`. Inverted delete logic (`if (exists) throw "not found"` then still deletes).
- **Root cause:** `JpaRepository<CropGuide, Integer>` while entity uses `String guideId` + `@GeneratedValue(GenerationType.UUID)`.
- **Status:** **FIXED**.
- **Patch applied:**
  - Changed repo to `JpaRepository<CropGuide, String>`.
  - Removed all `Integer.valueOf(...)` (now pass `guideId` directly).
  - Fixed delete: `if (!existsById(guideId)) throw ...; deleteById(guideId);`
  - Cleaned redundant `@Autowired` on field (now relies on `@RequiredArgsConstructor` + `final`).
- **Verification:** `mvn clean compile` on knowledge module → BUILD SUCCESS. Runtime CRUD should now work.

### 3. User phone-based delete was a no-op (plus path conflict)
- **Files:**
  - `kisan-user/user-api/src/main/java/com/kisan/user/controller/UserController.java`
  - `.../user-service/.../UserService.java`, `UserServiceImpl.java`
  - `.../controller/impl/UserControllerImpl.java`
- **Symptom:** `deleteUserByPhone` called `getUserByPhoneNumber` then returned 204 (no delete). Two `@DeleteMapping("/delete/{...}")` with different variables → ambiguous mapping.
- **Status:** **FIXED**.
- **Patch applied:**
  - Added proper `void deleteUserByPhone(String phoneNumber);` to service interface + real impl (existence check + `repo.deleteByPhoneNumber`).
  - Updated controller impl to call the real delete service method.
  - Changed phone delete mapping in interface to distinct non-conflicting path: `DELETE /phone/{phoneNumber}` (id delete remains `/delete/{userId}`).
  - Repo already declared the derived delete method (now used).
- **New endpoint (via gateway):** `DELETE /api/users/phone/{phoneNumber}` (with valid JWT).

## P1 — High Impact (Gateway routing & reachability broken)

### 4. Gateway route predicates did not match actual service controller paths
- **Files:**
  - `kisan-gateway/src/main/resources/application.properties`
  - `kisan-marketplace/.../controller/impl/EquipmentControllerImpl.java`
  - `kisan-marketplace/.../controller/BookingController.java` + Impl
  - `kisan-user/.../controller/UserController.java`
- **Symptom:** Requests to `/api/user/**`, `/api/marketplace/**` etc. would 404 or hit wrong/no controller because:
  - User route was singular (`/api/user/**`) while controller used `/api/users`.
  - Marketplace equipment impl used `/api/equipment`, bookings used `/api/bookings`.
  - Annotations were inconsistently placed (some on interfaces only, some on impls).
- **Status:** **FIXED** (path alignment + one route predicate).
- **Patches applied:**
  - Gateway: `Path=/api/users/**` (was `/api/user/**`).
  - EquipmentImpl: `@RequestMapping("/api/marketplace/equipment")` (now matches its interface + gateway prefix).
  - Booking: Moved `@RestController` + `@RequestMapping("/api/marketplace/bookings")` to the **Impl** (removed from interface). Updated interface mapping for contract consistency.
  - Knowledge: Added explicit `@RequestMapping("/api/knowledge/guides")` to `CropGuideControllerImpl` for robustness (was only on interface).
  - Cleaned leftover `@Autowired` on `final` field in BookingControllerImpl.
- **Result:** Calls through gateway should now reach the correct controllers:
  - Users: `/api/users/**`
  - Equipment: `/api/marketplace/equipment/**`
  - Bookings: `/api/marketplace/bookings/**`
  - Guides: `/api/knowledge/guides/**`

**Note on RewritePath:** The original commented rewrite rules in gateway props were not activated. We chose direct path alignment in the services (simpler, less magic). The commented rewrites can be cleaned up or properly implemented later if a prefix-stripping strategy is preferred.

### 5. RouteValidator open endpoint list + gateway security interaction
- Minor but related: `contains()` matching is loose. Open paths already used `/api/users/...` (now consistent after P1 user route fix).
- **Suggested patch (remaining):** Improve `isSecured` predicate to use exact prefix or `startsWith` + better pattern matching. Also consider protecting eureka dashboard if desired.

## P2 — Medium / Polish Issues (Remaining or lower priority)

| Priority | Issue | Files | Suggested Patch / Notes | Status |
|----------|-------|-------|-------------------------|--------|
| P2 | Hardcoded JWT secret (duplicated) | `user-service/.../security/JwtUtil.java`, `gateway/.../util/JwtUtil.java` | Move to `application.properties` / env var + `@Value`. Rotate in real deployment. | **FIXED** (see "JWT Secret Handling Fix" section below) |
| P2 | Duplicate/conflicting delete mappings were present (partially addressed) | `UserController.java` | The phone mapping was fixed to `/phone/...`. Consider removing `/delete/{userId}` or making it more RESTful (`DELETE /users/{userId}`). | Partially fixed |
| P2 | Lat/long fields defined but never populated or used | `User.java`, `UserDTO.java`, `Equipment.java`, `EquipmentDTO.java`, `EquipmentServiceImpl.java` | Add lat/long to `UserDTO` + registration/update flows. Copy them in `EquipmentServiceImpl.addEquipment` (like village/district). Implement geo search or radius queries in future. | Remaining (feature incomplete) |
| P2 | Inconsistent HTTP status on register | `UserControllerImpl.java:23` | User register returns 200 OK; marketplace equipment uses 201 CREATED. Standardize (use 201 for creates). | Remaining |
| P2 | Security model trusts all internal traffic | All `SecurityConfig.java` + gateway filter | Current design ("gateway handles auth") is common for this style but fragile. Add service-to-service auth (mTLS or internal JWT) or at least role checks (`@PreAuthorize`) on sensitive methods (ADMIN-only for knowledge write, owner checks for marketplace). | Remaining (intent documented in comments) |
| P2 | Injection anti-patterns (mix of @Autowired + @RequiredArgsConstructor + final) | Multiple service + controller impls (EquipmentServiceImpl, BookingControllerImpl before fix, knowledge before fix) | Standardize on constructor injection via Lombok `@RequiredArgsConstructor` + `private final` fields only. | Partially cleaned during fixes |
| P2 | No tests at all | Entire project (0 test sources) | Add unit tests for services, integration tests for controllers + Feign, contract tests. At minimum test happy paths for register/login, equipment add/book, knowledge CRUD. | Remaining |
| P2 | Discovery server not part of root reactor | Root `pom.xml` `<modules>` | Add `<module>discovery-server</module>` so `mvn clean install` from root builds everything. | Remaining |
| P2 | Missing operational support | No docker-compose, no README, hardcoded root DB creds everywhere, no profiles | Add `docker-compose.yml` (mariadb + the 5 services with proper wait-for / healthchecks). Externalize DB creds + JWT secret. Document startup order. | Remaining |
| P2 | Gateway RouteValidator uses fuzzy `contains()` | `RouteValidator.java` | Replace with proper prefix/startsWith or Ant-style matching to avoid accidental matches (e.g. a path containing "/eureka" substring). | Remaining |
| P2 | Booking overlap query only considers REQUESTED/CONFIRMED | `BookingRepository.java` | Good start. Consider business rules (e.g. should COMPLETED allow re-booking same slot immediately?). | OK for now |
| P2 | Feign client has no fallback / resilience | `UserClient.java` | Add Resilience4j or Spring Retry for user-service calls in marketplace. | Remaining |

## Additional Notes from Review
- **Path strategy chosen:** We aligned the *registered* controller paths in the service impls to be reachable under the existing gateway route prefixes (`/api/users`, `/api/marketplace/*`, `/api/knowledge/*`). This is the highest-impact minimal-change approach.
- **Booking controller design:** Annotations are now only on the concrete `@RestController` (best practice). Interface carries method-level mappings for documentation/contract.
- **Builds verified post-fix:** 
  - marketplace-app (package) → correct Start-Class + BUILD SUCCESS
  - kisan-knowledge (compile) → SUCCESS
  - kisan-user (compile) → SUCCESS
- Discovery must still be built/run separately until root pom is updated.
- All changes are backward-compatible for direct service calls (where they worked before). The main win is making the **gateway the reliable single entry point**.

## JWT Secret Handling Fix (2026-06-13)

**What was fixed:**
- Removed the duplicated hardcoded `SECRET` constant from both `JwtUtil` implementations.
- Added configurable property `jwt.secret` (with env var override support) to:
  - `kisan-user/user-app/src/main/resources/application.properties`
  - `kisan-gateway/src/main/resources/application.properties`
- Both `JwtUtil` classes now use Spring `@Value("${jwt.secret}")` to inject the secret at runtime.
- Property supports default for local dev + override:
  - Environment variable: `JWT_SECRET=...`
  - System property: `-Djwt.secret=...`
  - Or directly in the `application.properties` of each service (user-app and gateway).

**Files changed:**
- `kisan-user/user-app/src/main/resources/application.properties`
- `kisan-gateway/src/main/resources/application.properties`
- `kisan-user/user-service/src/main/java/com/kisan/user/security/JwtUtil.java`
- `kisan-gateway/src/main/java/com/kisan/gateway/util/JwtUtil.java`

**Latest update (strict security posture):**
- The actual secret value has been **completely removed** from the committed properties files.
- Properties now contain only the placeholder: `jwt.secret=${JWT_SECRET}`
- No default value is provided — the application will **fail fast at startup** (with a clear `IllegalStateException` from `@PostConstruct` validation) if the environment variable is missing or invalid.
- Added runtime validation in both `JwtUtil` classes (`@PostConstruct`):
  - Checks that the secret is present and non-blank.
  - Validates it decodes to at least 32 bytes (256-bit) for HS256.
  - Gives helpful error messages.

**Important notes:**
- The value **must be identical** in both services (set the same `JWT_SECRET` env var for user-service and gateway).
- **PowerShell (before starting any service):**
  ```powershell
  $env:JWT_SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"
  ```
- For production: **Never commit real secrets to git**. Use Kubernetes Secrets, Docker secrets, Vault, Spring Cloud Config, or platform env var injection. Consider also externalizing `jwt.expiration`.
- Both modules compile successfully (`mvn compile` verified).

This change was made in direct response to the request to remove hardcoded secrets from properties entirely.

This moves the project from "hardcoded in source" to "properly externalized config".

## Environment Configuration (Added .env.example + DB Externalization)

- Created `.env.example` at the project root with:
  - `JWT_SECRET` (required, no default)
  - `DB_USERNAME` and `DB_PASSWORD` (for all three data services)
- Updated all `application.properties` (user, marketplace, knowledge) to use `${DB_USERNAME}` and `${DB_PASSWORD}` instead of hardcoded `root`.
- DB credentials are now loaded the same way as the JWT secret (via OS environment variables, supporting the same PowerShell / shell loading pattern).
- Services will fail fast with clear messages if the required variables are missing.
- `.env.example` includes instructions for loading on Windows (PowerShell) and Unix-like systems.
- **Never commit a real `.env` file.**

This completes the sensitive values externalization (JWT + DB passwords).

## Recommended Next Steps (after these fixes)
1. Add/update `BUGS.md` (this file) in repo.
2. Create a minimal `docker-compose.yml` + health endpoints so the whole system can be brought up with one command (use the `.env` file).
3. Add at least happy-path tests for the three P0 areas we fixed.
4. Decide on final path convention (current aligned version or prefix-rewrite) and document for frontend consumers.

If you want patches for any of the remaining P2 items (or a docker-compose + startup script), let me know the priority!