# Fiend

A mobile application acting as a YouTube Music wrapper that plays songs without ads. This project bypasses ads using Brave's core adblock engine (written in Rust) by intercepting network requests natively without relying on WebViews. 

## Features
- **Apple Music-style UI**: A clean, premium user interface built with Jetpack Compose.
- **Native Adblocking**: Uses `adblock-rust` running locally via JNI to filter OkHttp requests.
- **Direct API Integration**: Interacts directly with the YouTube InnerTube API to fetch recommendations and stream URLs natively.
- **Background Playback**: Powered by `androidx.media3` ExoPlayer for robust background audio streaming.

## Setup Instructions

### Prerequisites
To build this project, you **must** have Rust and Cargo installed so Android Studio can compile the native adblocker engine.

#### 1. Install Rust
- Go to the official Rust website: [https://rustup.rs/](https://rustup.rs/)
- Download and run `rustup-init.exe` (on Windows) or follow the curl instructions for macOS/Linux.
- Proceed with the default installation (Type `1` and press Enter).
- *If prompted on Windows, install the Microsoft Visual Studio C++ Build Tools.*
- **Important:** Close and restart Android Studio after installing Rust so it recognizes the `cargo` command in your PATH.

#### 2. Install Android NDK Targets for Rust
Open a terminal (or the terminal inside Android Studio) and run the following commands to add the Android compilation targets:

```powershell
rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android
```

Then, install the cargo-ndk helper tool:
```powershell
cargo install cargo-ndk
```

### Building the Project
Once the prerequisites are installed:
1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Click **Run** (the green play button).
4. Gradle will automatically invoke Cargo to compile the `rust_adblock` folder into `.so` libraries for your device architecture, bundle them into the APK, and launch the app!
