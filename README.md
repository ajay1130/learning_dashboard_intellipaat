# Learning Dashboard (Android)

Kotlin · Jetpack Compose · Hilt · Room · DataStore · Coroutines/Flow · Navigation Compose

**[Demo video](https://github.com/ajay1130/learning_dashboard_intellipaat/releases/download/v1.0/learning-dashboard-demo.webm) · [Download APK](https://github.com/ajay1130/learning_dashboard_intellipaat/releases/download/v1.0/learning-dashboard-v1.0.apk) · [Release v1.0](https://github.com/ajay1130/learning_dashboard_intellipaat/releases/tag/v1.0)**

**Demo login:** `demo@learn.com` / `password123` · **Tests:** `./gradlew testDebugUnitTest` · **APK:** `./gradlew assembleDebug` · [Screenshots](docs/screenshots)

## 1. Architecture

Clean Architecture with MVVM: `ui/` (Compose screen → ViewModel) → `domain/` (use cases, models, repository interfaces, pure Kotlin) → `data/` (repository implementations → fake API + Room/DataStore).

- Dependencies point inward. ViewModels only know use cases, and `data` implements interfaces owned by `domain`, so replacing the fake API with Retrofit touches only `data/` and `DataModule`.
- Each screen exposes one immutable `StateFlow<UiState>` (a sealed type: loading, content, empty, error). Composables are stateless and only render it.
- Business rules (progress, credential validation) live in `domain/` and are unit tested on the JVM. Hilt wires the graph, and tests pass fakes through constructors.
- I chose this because it scales to a team: each layer can be tested and replaced on its own. Next step: one Gradle module per layer, so the compiler enforces the boundaries.

## 2. Offline support

Room is the single source of truth. Screens observe database `Flow`s, and `refreshCourses()` fetches from the API and writes in one transaction.

- Refresh fails with a cache: courses stay visible with an "offline" banner. Refresh fails with no cache: an error screen with Retry.
- Marking a lesson complete writes to Room, and both screens update reactively. Progress is derived in SQL (`COUNT`/`SUM` of lessons), never stored separately, so it can't drift. The mock payload embeds each course's lessons so details work offline too.
- A refresh never undoes local progress: lessons completed on the device stay completed when server data is merged.
- The mock API checks real connectivity, so airplane mode exercises the real offline path.

## 3. Security

The demo token sits in DataStore. In production I'd encrypt tokens with a key held in the **Android Keystore** (e.g. Tink with DataStore, or EncryptedSharedPreferences), exclude them from backups, use short-lived access tokens refreshed by an OkHttp `Authenticator`, pin the API certificate, and clear tokens and cached data on logout (done here).

## 4. Scale (1M users, hundreds of courses)

1. **Paging 3 + RemoteMediator** for the catalogue, and a separate lessons endpoint per course.
2. **Offline write sync:** an outbox table for completions, pushed by WorkManager with idempotent requests and server timestamps.
3. **Network efficiency:** ETag/HTTP caching, background refresh, CDN for media.
4. **Observability:** Crashlytics, performance traces, remote config/feature flags for staged rollouts.
5. **Codebase:** feature modules, exported Room schemas with migrations, UI tests in CI.

## 5. Second platform (iOS)

The same layers in Swift: SwiftUI views with an `@Observable` ViewModel per screen exposing a state enum, protocol-based repositories using `async/await` and `URLSession`, and SwiftData (or GRDB) as the offline source of truth. Tokens go in the **Keychain**. `NavigationStack` is driven by session state, dependencies are passed through initializers, and tests use XCTest with protocol fakes. Kotlin Multiplatform could later share the domain and data layers.
