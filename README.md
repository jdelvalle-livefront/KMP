# ArtApp

A small Kotlin Multiplatform mobile app for Android and iOS with two screens —
a list of artworks and a detail view — backed by the [Cleveland Museum of Art
Open Access API](https://openaccess-api.clevelandart.org/). The API is public
and requires no key or authentication, so the project clones and runs with no
setup.

## What it is

- **List screen**: paginated artworks (title, artist, date, thumbnail).
- **Detail screen**: full image, artist, date, medium, place of origin,
  credit line, and description.
- Loading, empty, and error states are modeled explicitly and handled the
  same way on both screens, including a retry action.

## Running it

No API keys, no local config files, no extra setup — clone and run.

### Android

Open the project in Android Studio and run the `androidApp` configuration,
or from the command line:

```bash
./gradlew :androidApp:assembleDebug
```

### iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run it on a simulator. The
shared Kotlin framework builds automatically as part of the Xcode build.

### Tests

All tests live in `commonTest` and run on both targets:

```bash
./gradlew :shared:testAndroidHostTest
./gradlew :shared:iosSimulatorArm64Test
```

## Architecture

```
shared/src/
  commonMain/kotlin/.../
    data/
      remote/
        ArtworkApi.kt          # Ktor client
        HttpClientFactory.kt   # HttpClient config; expect/actual only for the engine
        dto/                   # DTOs + mappers to domain models
      ArtworkRepository.kt     # interface + default implementation
    domain/
      model/                   # Artwork, ArtworkDetail, ArtworkResult/ArtworkError
    ui/
      list/     ListScreen.kt, ListViewModel.kt, ListUiState.kt
      detail/   DetailScreen.kt, DetailViewModel.kt, DetailUiState.kt
      components/              # shared image/loading/error composables
      theme/
    App.kt                     # navigation
  commonTest/kotlin/...        # every test — data, mappers, ViewModels
  androidMain/                 # OkHttp engine only
  iosMain/                     # Darwin engine only
androidApp/                    # Android entry point
iosApp/                        # thin SwiftUI wrapper around the shared UI
```

`commonMain` is maximized on purpose: networking, parsing, mapping, UI state,
and error handling all live in shared code. `androidMain`/`iosMain` are used
only for what is genuinely platform-specific — the Ktor engine (OkHttp vs.
Darwin) — which is also the only `expect`/`actual` pair in the project.

The domain model reflects what the UI needs, not the shape of the API
response; the DTO → domain mapping is explicit and lives next to the DTOs.
Screen state is a `sealed interface` (`Loading` / `Content` / `Error`, plus
`Empty` for the list), and network/API errors are mapped to a typed
`ArtworkError` rather than letting exceptions reach the UI layer.

## Technical decisions and trade-offs

### Why Compose Multiplatform instead of native UI

This is the most consequential decision in the project, so it's worth being
explicit about what it buys and what it costs.

**What's gained**: one UI implementation shared across Android and iOS
instead of two. For an app this size — two screens, no platform-specific
interactions — that removes real duplication: the screens, the loading/error
states, and the navigation only need to be written, reviewed, and tested
once. It also means a bug fixed in the shared UI is fixed on both platforms
simultaneously, which matters more as an app grows.

**What's lost**: fine-grained platform feel doesn't come for free. Compose
Multiplatform renders its own UI on iOS rather than using UIKit/SwiftUI
directly, so things like exact system gestures, fine-grained accessibility
behavior tuned to each platform's conventions, and the subtle motion/timing
that makes an app "feel" native to a platform all require deliberate,
separate work to get right — they don't come from the framework by default
the way they would in a fully native SwiftUI or Jetpack Compose (Android-only)
app.

**Where the opposite call would be right**: a product where that native feel
is itself a competitive feature — a camera app, something leaning heavily on
platform-specific APIs or system integrations, or a team organized around
platform specialists who'd rather own their platform end-to-end — is a much
better fit for native UI per platform. For a small, content-driven app like
this one, the developer judged that sharing the UI pays off more than it
costs.

### Why no dependency injection framework

Dependencies are built once at each platform's real entry point
(`MainActivity` on Android, `MainViewController` on iOS) and passed down
through constructor parameters. With two screens and a handful of classes,
the developer considered a DI framework (Koin, Hilt) to be added indirection
without a real problem to solve — plain constructor injection is enough to
keep everything testable and explicit.

### Testing approach

- **Ktor `MockEngine`** tests for the network layer, using real JSON
  captured from the API — covering normal responses, missing optional
  fields, and HTTP errors.
- **Mapper tests** for DTO → domain nullability edge cases (an artwork with
  no image, no listed creator, and so on).
- **ViewModel tests** with `kotlinx-coroutines-test`, covering state
  transitions (loading → content, loading → error, retry) against a fake
  repository — no network involved.

Everything lives in `commonTest` so the same suite runs against both the
Android host and the iOS simulator target.

### `description` and HTML

The API returns the `description` field as raw HTML. Rather than pull in an
HTML renderer or sanitizer for a single field, the detail screen strips tags
with a simple regex and displays plain text. This is a known limitation —
inline formatting or links in the original text are lost — accepted here as
out of scope for the size of this project.

## Out of scope

Offline cache, search, filters, infinite pagination, CI, elaborate
animations, analytics, and a DI framework are all intentionally not
implemented. If one thing were added next, it would be simple pagination
("load more" on scroll) — the API and repository are already shaped to
support it (`page`/`limit` are already parameters on `ArtworkRepository`),
only the UI trigger is missing.

## Third-party code

No third-party code was copied into this project. All functionality is
built on public libraries pulled in via Gradle (Ktor, kotlinx.serialization,
kotlinx.coroutines, Coil, Navigation Compose, Compose Multiplatform) — see
`gradle/libs.versions.toml` for the full list and versions.
