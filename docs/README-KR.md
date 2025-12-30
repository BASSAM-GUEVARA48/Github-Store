<div align="center">
<img src="https://github.com/rainxchzed/Github-Store/blob/main/composeApp/src/commonMain/composeResources/drawable/app-icon.png" width="200" alt="프로젝트 로고"/>
</div>

<h1 align="center">GitHub Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="라이선스" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"/></a>
  <a href="#"><img alt="플랫폼" src="https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop-brightgreen"/></a>
  <a href="https://github.com/rainxchzed/Github-Store/releases">
    <img alt="릴리스" src="https://img.shields.io/github/v/release/rainxchzed/Github-Store?label=Release&logo=github"/>
  </a>
  <a href="https://github.com/rainxchzed/Github-Store/stargazers">
    <img alt="GitHub 스타" src="https://img.shields.io/github/stars/rainxchzed/Github-Store?style=social"/>
  </a>
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Koin" src="https://img.shields.io/badge/DI-Koin-3C5A99?logo=kotlin&logoColor=white"/>
</p>

<p align="center">
GitHub Store는 GitHub 릴리스를 위한 크로스플랫폼 “Play Store”입니다.
실제로 설치 가능한 바이너리를 제공하는 저장소를 찾아
한 곳에서 설치, 추적 및 업데이트할 수 있습니다.
</p>

<p align="center">
  <img src="../screenshots/banner.png" />
</p>

---

### 모든 스크린샷은 [screenshots/](screenshots/) 폴더에서 확인할 수 있습니다.

<img src="/screenshots/preview.gif" align="right" width="320"/>

## ✨ GitHub Store란?

GitHub Store는 Kotlin Multiplatform(Android + Desktop)으로 제작된 앱으로,
GitHub Releases를 깔끔한 앱 스토어 형태의 경험으로 바꿔줍니다.

- 실제로 설치 가능한 에셋(APK, EXE, DMG, AppImage, DEB, RPM 등)을 제공하는
  저장소만 표시합니다.
- 사용 중인 플랫폼을 감지하여 올바른 설치 파일을 제공합니다.
- 항상 최신에 게시된 릴리스를 설치하며, 변경 로그를 표시하고
  Android에서는 업데이트 알림을 받을 수 있습니다.
- 통계, README, 개발자 정보를 포함한 세련된 상세 화면을 제공합니다.

---

## 🔃 다운로드

<a href="https://github.com/rainxchzed/Github-Store/releases">
   <image src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png" height="80"/>
 </a>

<a href="https://f-droid.org/en/packages/zed.rainxch.githubstore/">
   <image src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>

> [!IMPORTANT]
> macOS에서는 Apple이 GitHub Store를 악성 소프트웨어로부터
> 안전한 앱인지 확인할 수 없다는 경고가 표시될 수 있습니다.
> 이는 App Store 외부에서 배포되며 아직 공증되지 않았기 때문입니다.
> 시스템 설정 → 개인정보 보호 및 보안 → 그대로 열기를 통해 실행할 수 있습니다.

---

## 🚀 주요 기능

- **스마트한 탐색**
    - “Trending”, “Recently Updated”, “New” 홈 섹션 제공 (시간 기반 필터).
    - 유효한 설치 파일을 포함한 저장소만 표시됩니다.
    - 플랫폼 인식 랭킹으로 Android / Desktop 사용자에게
      관련성 높은 앱을 우선 표시합니다.

- **최신 릴리스 설치**
    - 각 저장소에 대해 `/releases/latest` 사용.
    - 최신 릴리스의 에셋만 표시.
    - 단일 “Install latest” 버튼과 해당 릴리스의 모든 설치 파일 목록 제공.

- **풍부한 상세 화면**
    - 앱 이름, 버전, “Install latest” 버튼.
    - 스타 수, 포크 수, 열린 이슈.
    - 렌더링된 README(앱 정보).
    - Markdown을 지원하는 최신 릴리스 노트.
    - 플랫폼 및 파일 크기가 표시된 설치 파일 목록.

- **크로스플랫폼 UX**
    - Android: APK를 시스템 패키지 설치 프로그램으로 열고,
      로컬 데이터베이스에서 설치를 추적하며 업데이트 표시.
    - Desktop(Windows / macOS / Linux):
      설치 파일을 사용자의 다운로드 폴더에 저장하고
      기본 프로그램으로 실행합니다.

- **외형 및 테마**
    - 모든 플랫폼에서 **Material 3 Expressive** 디자인 사용.
    - Android에서 Material You 동적 색상 지원(가능한 경우).
    - OLED 기기를 위한 선택적 AMOLED 블랙 모드.

- **보안 및 검사(Android)**
    - API 요청 제한을 늘리기 위한 GitHub OAuth(Device Flow) 로그인(선택).
    - 설치 전 APK의 권한과 트래커를 확인할 수 있는
      “Open in AppManager” 기능.

---

## 🔍 내 앱은 어떻게 GitHub Store에 표시되나요?

GitHub Store는 비공개 인덱싱이나 수동 큐레이션을 사용하지 않습니다.  
다음 조건을 충족하면 프로젝트가 자동으로 표시될 수 있습니다.

1. **GitHub의 공개 저장소**
    - 저장소 가시성은 `public`이어야 합니다.

2. **최소 1개의 게시된 릴리스**
    - GitHub Releases를 통해 생성된 릴리스여야 합니다(태그만은 불가).
    - 최신 릴리스는 초안(draft) 또는 사전 릴리스(prerelease)가 아니어야 합니다.

3. **최신 릴리스에 설치 가능한 에셋 포함**
    - 지원되는 확장자를 가진 파일이 최소 1개 이상 있어야 합니다:
        - Android: `.apk`
        - Windows: `.exe`, `.msi`
        - macOS: `.dmg`, `.pkg`
        - Linux: `.deb`, `.rpm`, `.AppImage`
    - GitHub에서 자동 생성된 소스 코드 아카이브는 무시됩니다.

4. **검색 / 토픽을 통해 발견 가능**
    - 공개 GitHub Search API를 통해 저장소를 검색합니다.
    - 토픽, 언어, 설명이 랭킹에 영향을 줍니다.
    - 일정 수 이상의 스타가 있으면 노출 가능성이 높아집니다.

---

## 🧭 GitHub Store 작동 방식(개요)

1. **검색**
    - 플랫폼 인식 쿼리를 사용하여 `/search/repositories` 호출.
    - 토픽, 언어, 설명 기반의 간단한 점수 계산.
    - 아카이브된 저장소는 제외.

2. **릴리스 및 에셋 확인**
    - `/repos/{owner}/{repo}/releases/latest` 호출.
    - 플랫폼에 맞는 에셋이 있는지 확인.
    - 적절한 파일이 없으면 결과에서 제외.

3. **상세 화면**
    - 저장소 정보: 이름, 소유자, 설명, 스타, 포크, 이슈.
    - 최신 릴리스: 태그, 게시 날짜, 변경 로그, 에셋.
    - 기본 브랜치의 README를 “앱 정보”로 표시.

4. **설치 흐름**
    - “Install latest” 클릭 시:
        - 현재 플랫폼에 가장 적합한 에셋 선택.
        - 다운로드 진행.
        - OS 설치 프로그램으로 위임.
        - Android에서는 로컬 DB에 설치 기록 저장.

---

## ⚙️ 기술 스택

- **최소 Android SDK: 24**

- **언어 및 플랫폼**
    - Kotlin Multiplatform (Android + JVM Desktop)
    - Compose Multiplatform UI (Material 3)

- **비동기 및 상태 관리**
    - Kotlin Coroutines + Flow
    - AndroidX Lifecycle

- **네트워크 및 데이터**
    - Ktor 3
    - Kotlinx Serialization JSON
    - Kotlinx Datetime
    - Room + KSP(Android)

- **의존성 주입**
    - Koin 4

- **내비게이션**
    - JetBrains Navigation Compose

- **인증 및 보안**
    - GitHub OAuth (Device Code Flow)
    - Androidx DataStore

- **미디어 및 Markdown**
    - Coil 3
    - multiplatform-markdown-renderer-m3

- **로깅 및 도구**
    - Kermit
    - Compose Hot Reload
    - ProGuard / R8

---

## ✅ GitHub Store를 사용하는 이유

- **GitHub 릴리스를 일일이 찾을 필요 없음**
- **설치된 앱 추적**
- **항상 최신 버전 설치**
- **Android와 Desktop에서 일관된 경험**
- **오픈 소스이며 확장 가능**

---

## ⚠️ 면책 조항

GitHub Store는 GitHub에 게시된
서드파티 개발자의 릴리스 에셋을 발견하고 다운로드하는 데 도움을 줄 뿐입니다.

GitHub Store를 사용함으로써,
다운로드한 소프트웨어를 자신의 책임 하에 설치하고 실행하는 것에 동의하게 됩니다.

---

## 📄 라이선스

GitHub Store는 **Apache License, Version 2.0** 하에 배포됩니다.

```
Copyright 2025 rainxchzed

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this project except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```