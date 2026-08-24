# Fiend - Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]
### Added
- **Android Project Setup:** Initialized Android Gradle project with Kotlin DSL.
- **Compose UI:** Configured Jetpack Compose inside `build.gradle.kts` and created a basic Apple Music inspired UI in `MainScreen.kt`.
- **Rust Adblock Core:** Created the `rust_adblock` crate that integrates Brave's `adblock-rust` engine.
- **JNI Bridge:** Added `AdblockEngine.kt` and `lib.rs` to allow Kotlin to call the Rust native library.
- **Network Interception:** Added `AdblockInterceptor.kt` to pipe OkHttp requests through the Rust engine.
- **YouTube InnerTube API:** Added `InnerTubeClient.kt` to natively fetch YouTube Music recommendations and stream URLs without WebViews.
- **Background Player:** Added `PlayerViewModel.kt` utilizing `androidx.media3` ExoPlayer for robust background audio streaming.

- **Automated Rust Builds**: Integrated the `mozilla.rust-android-gradle.rust-android` plugin to automatically trigger Cargo builds when the app is run from Android Studio.
- **Documentation**: Added a `README.md` containing full setup instructions for installing Rust, Cargo, and the required Android NDK targets on Windows.
- **UI Fallback Notification**: Added a startup Toast message in `MainActivity.kt` to explicitly warn the user if the adblock engine fails to load, instead of silently disabling it.

### Changed
- **UI Redesign**: Completely overhauled `MainScreen.kt` to replicate the official **YouTube Music** dark theme. Replaced vertical lists with horizontal carousels ("Mixed for you", "New releases"), added top category pills, a bottom navigation bar, and a persistent mini-player docked above the navigation.
- Updated `libs.versions.toml` to manage Kotlin 2.0 and Compose Compiler plugin explicitly to avoid classloading errors.

### Fixed
- Handled `UnsatisfiedLinkError` in `AdblockEngine.kt` by wrapping `System.loadLibrary` in a try/catch, allowing the app to gracefully launch even if the Rust engine is not compiled on the user's host machine.
- Resolved Gradle "extension already registered" conflicts by delegating Kotlin compiler configuration to the native Android Application plugin.
