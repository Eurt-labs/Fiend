# ⚡ Fiend v2.1.0 — The Ultimate Audiophile YouTube Music Client

> **Crafted with passion by Eurt Labs & ACRO**  
> *Experience studio-grade Bit-Perfect sound, resilient high-speed streaming, immersive Liquid Glass design, and universal device support.*

---

## 🌟 Overview

**Fiend v2.1.0** represents a massive leap forward. Built from the ground up for audiophiles, music enthusiasts, and daily listeners, this milestone release introduces an authentic **AMOLED Obsidian & Electric Cyan** brand identity, completely neutralizes mid-track streaming halts, upgrades our real-time **Fiend Jam** collaborative listening rooms, and delivers complete device compatibility covering **100% of Android phones and tablets**.

---

## 💎 Major Highlights & What's New

### 🎧 1. Audiophile-Grade Audio Engine & Bit-Perfect Playback
* **Hardware Bit-Perfect USB DAC Output:** Direct hardware bypass allows high-resolution audio streams to pass straight to your external DAC/Amp without Android's standard 48 kHz OS resampler degradation.
* **AutoEQ Headphone Calibration:** Over 2,500 curated parametric EQ profiles tailored for specific IEMs, open-backs, and closed-back headphones.
* **15-Band Parametric Equalizer:** Precise frequency adjustments with customizable Q-factor and peak/shelf filters.
* **Lossless FLAC Addon Support:** Modular architecture enabling 24-bit Hi-Res audio playback.
* **Loudness Normalization & Automix:** ReplayGain / LUFS volume leveling to protect your hearing across disparate tracks, accompanied by intelligent beat-matched Automix transitions and crossfading.

---

### 🛡️ 2. Resilient Streaming Engine (Zero Mid-Song Stops)
* **Neutralized CDN Socket Drops:** Completely overhauled `ChunkedRangeDataSource` with automatic socket re-establishment and exponential retry logic. Prevents Google Video CDN idle keep-alive timeouts from severing streams on high-speed Wi-Fi and 5G networks.
* **Smart Playlist Advance:** Fixed playback auto-pause behavior so multi-track playlists and Spotify-imported queues automatically skip unplayable or restricted media rather than freezing the entire queue.
* **Non-Blocking Cache Buffering:** Audio packets are aggressively pre-buffered with automated recovery if format renegotiation occurs mid-stream.

---

### 🎨 3. Liquid Glass UI & AMOLED Obsidian Identity
* **Authentic Signature Palette:** Upgraded to high-contrast AMOLED Obsidian Black (`#050811`), Midnight Navy (`#0A0F1D`), Electric Cyan (`#00D2FF`), and Neon Violet (`#818CF8`).
* **Real-Time Blur & Refraction:** Backdrop shaders that blur album artwork into frosted glass surfaces with smooth spring physics.
* **Fluid Animated Splash Screen:** Staggered entrance scaling, dual-ring ambient cyan breathing glow, and dynamic 4-bar equalizer micro-animations.
* **Sharp App Icon Presets:** Completely refreshed high-contrast icons for **Original Signature**, **Desktop Cyan**, **Pure White**, and **Obsidian Black**.
* **Cleaned Settings Experience:** Streamlined cards with direct links to official Telegram channels and complete scrubbing of deprecated third-party cards.

---

### 👥 4. Fiend Jam — Collaborative Real-Time Listening
* **Low-Latency WebSocket Relay:** Connected to high-availability server infrastructure (`wss://jam.clashprojects.qd.je/ws`) ensuring instantaneous room creation, participant tracking, and synchronization.
* **Shared Queue Controls:** Listen in sync with friends anywhere in the world with host permissions and synced playback positions.
* **Electric Cyan Theme:** Jam sheets and controls updated to reflect the new electric cyan design system.

---

### 📜 5. Real-Time Synced Lyrics & Discovery
* **Millisecond-Synchronized Lyrics:** Direct integration with LRCLIB featuring live word-by-word animations.
* **Multi-Language Romanization:** Automatic phonetic transliteration for Japanese (Romaji), Korean, and non-Latin alphabets.
* **Aesthetic Lyrics Quote Cards:** Export and share favorite lyrics verses as customized graphic cards.
* **Spotify Canvas Support:** Enjoy animated video loops from supported tracks directly in the expanded player.

---

### 🌐 6. Ecosystem Bridges
* **Fiend Desktop Companion:** Seamless workflow continuity with the official [Fiend Desktop](https://github.com/acro777x/Fiend-Desktop) app for Windows, macOS, and Linux.
* **Stash Ecosystem:** Direct interoperability with [Stash](https://github.com/Eurt-labs/Stash) companion tools.
* **In-App Update Radar:** Automated checks tracking official GitHub releases to keep your build continuously up to date.

---

## 📱 Universal Device Support Matrix

We have compiled dedicated release binaries covering **every Android processor architecture and OS version**:

| Package File | Target Audience & Architecture | Min Android Version | Download Size |
| :--- | :--- | :--- | :--- |
| **`Fiend-v2.1.0-universal.apk`** | **Universal Recommended:** Bundles all 4 architectures (`arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`). Works on all modern phones, tablets & emulators. | Android 10+ (API 29+) | **35.79 MB** |
| **`Fiend-v2.1.0-arm64-v8a.apk`** | **Modern Compact:** Optimized for 95%+ of modern smartphones (Samsung, Google Pixel, OnePlus, Xiaomi, Nothing, etc.) for a smaller download size. | Android 10+ (API 29+) | **25.96 MB** |
| **`Fiend-v2.1.0-android7-universal.apk`** | **Maximum Compatibility:** All architectures with backward support for Android 7.0 Nougat, 8.0 Oreo, and 9.0 Pie. | Android 7.0+ (API 24+) | **27.37 MB** |

---

## 🔒 Verification & SHA-256 Checksums

Every APK in this release is signed using our production release key. Verify your download with the SHA-256 digests below:

```text
Fiend-v2.1.0-universal.apk
5A1B56EB6CE02A8487AFCB6171FF188A91FE933A48A67E965BF67D6F3E049B7E

Fiend-v2.1.0-arm64-v8a.apk
D0E198459C1D6A14150EE59756FF1DDD5C162DA0E810063E406EC188C50C1DAD

Fiend-v2.1.0-android7-universal.apk
288171F56585DDDDBB8F69AA792432D00E660CB880F08878D8B67CCF9AC3FBDC
```

---

## 🛠️ Detailed Changelog

```text
### Audio & Playback Engine
* FIX: Resolved mid-song playback freezing caused by Google Video CDN dropping idle keep-alive sockets.
* FIX: Added automatic exponential reconnect loop in ChunkedRangeDataSource during buffer replenishment.
* FIX: Fixed EOF byte counter accumulation that triggered premature stream terminations.
* FIX: Prevented player pauses when resolving unavailable multi-track media items; player now automatically skips ahead.
* PERF: Optimized native memory alignment for 16 KB page sizes on Android 15.

### UI & Styling
* FEAT: Introduced authentic AMOLED Obsidian Black (#050811) and Electric Cyan (#00D2FF) signature color scheme.
* FEAT: Redesigned FiendBrandSplashScreen with staggered scaling, ambient radial breathing glow, and bouncing audio bars.
* FEAT: Generated high-contrast emblems for all 4 App Icon presets (Default Signature, Desktop Cyan, Pure White, Obsidian Black).
* FEAT: Modernized Settings layout and search index with 'by Eurt Labs and ACRO' identity.
* REFACTOR: Completely removed deprecated Discord cards and search index entries.

### Jam Session
* FIX: Restored operational WebSocket server relay endpoint to wss://jam.clashprojects.qd.je/ws.
* UI: Updated Jam screen buttons, banners, and bottom sheet to match the Electric Cyan theme.

### Architecture & Packaging
* FEAT: Added multi-ABI compilation producing Universal, ARM64, and Android 7.0+ backward-compatible release APKs.
* DOCS: Published fresh, dedicated README.md with complete architecture breakdown and ecosystem navigation.
```

---

## 💬 Community, Support & Links

* 💬 **Telegram Support Group:** [@fiend_music](https://t.me/fiend_music)
* 📢 **Announcements & Updates:** [@eurtXacro](https://t.me/eurtXacro)
* 🖥️ **Desktop Client:** [Fiend Desktop](https://github.com/acro777x/Fiend-Desktop)
* 🗃️ **Ecosystem Products:** [Stash](https://github.com/Eurt-labs/Stash)

---
*Fiend is an open-source, non-commercial application developed strictly for educational and personal research purposes. All media streams are resolved directly from public endpoints.*
