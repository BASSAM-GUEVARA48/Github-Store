<h1 align="center">Github-Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://github.com/XDdevv/Github-Store/releases"><img alt="Download" src="https://img.shields.io/github/v/release/XDdevv/Github-Store.svg?logo=github"/></a>
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
</p>

<p align="center">
A modern Multiplatform Open-source “Play Market” for GitHub.<br>
🚀 Discover, browse, and install open-source apps and tools directly from GitHub releases — for Android & Desktop.<br>
</p>

---

<!-- <p align="center">
<img src="previews/hero.png" width="90%"/>
</p> -->

---

## Features

- ✨ **Trending & Most Starred:** Home showcases hot repos (with releases!) by stars or trending.
- 🔎 **Fast Search:** By name, description, or README (only shows repos with installable releases).
- 📦 **Easy Install:** Installs APK, EXE, DMG, ZIP, etc. straight from open source releases.
- 📑 **Detailed Repo Pages:** See description, release changelogs, stars, forks, contributors, and developer info.
- 🔐 **GitHub Login:** Auth for starring and boosting API limits (optional for browsing).
- 🌙 **Beautiful UI:** Material 3, light & dark themes, responsive multiplatform layouts.
- 💻 **Multiplatform:** Native support for Android and Desktop (Windows/macOS/Linux).

---

<!-- Screenshots -->

<!-- <p align="center">
  <img src="previews/screens-home.png" width="40%"/>
  <img src="previews/screens-details.png" width="40%"/>
</p> -->

---

## Download

- Grab the latest [Android APK/Desktop package](https://github.com/XDdevv/Github-Store/releases) and start exploring open source gems!

---

## Tech Stack & Open-Source Libraries

- **Kotlin Multiplatform (KMP)** — shared logic for Android and Desktop
- **Jetpack Compose / Compose Multiplatform** — UI for Android, Windows, macOS, Linux
- **Ktor / OkHttp** — Modern HTTP & API client
- **GitHub REST API** — Search, Releases, README, Auth
- **Material 3** — Clean, modern UI
- **Others:** Accompanist, kotlinx.serialization, Coil (image loading), etc.

---

## Architecture

- **Clean Architecture** layered: Domain, Data, Presentation
- **Unidirectional Data Flow** with State Management (ViewModels/shared logic)
- **Open API:** Pure GitHub REST API, no backend required!

---

## Getting Started

Clone this repo
git clone https://github.com/XDdevv/Github-Store.git
cd Github-Store

Open with Android Studio (Giraffe+) for multiplatform builds!


- Run on Android: Select your device/emulator.
- Run Desktop: Select the desktop run target, or run `./gradlew run` from terminal.

Set your **GitHub OAuth App keys** in `local.properties` or as secrets in your CI for authentication.

---

## Roadmap

- [ ] Home, trending & most-starred repositories
- [ ] Search by name, description, readme
- [ ] Repo details with install options
- [ ] Auth via GitHub (deep link/callback)
- [ ] Stars, forks, developer profile
- [ ] Download managers, install progress
- [ ] (Planned) Recommendations, notifications

Contributions & feedback welcome! See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

```
Designed and developed by 2025 rainxchzed / Usmon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
---

**Find this project useful?**  
🌟 **Star this repo** and [follow me](https://github.com/XDdevv) for more open source tools!

---

_**Not affiliated with GitHub or Google. This is an independent, open source app store for GitHub releases—by developers, for developers.**_
