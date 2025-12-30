<div align="center">
<img src="https://github.com/rainxchzed/Github-Store/blob/main/composeApp/src/commonMain/composeResources/drawable/app-icon.png" width="200" alt="项目 Logo"/>
</div>

<h1 align="center">GitHub Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="许可证" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"/></a>
  <a href="#"><img alt="平台" src="https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop-brightgreen"/></a>
  <a href="https://github.com/rainxchzed/Github-Store/releases">
    <img alt="发布版本" src="https://img.shields.io/github/v/release/rainxchzed/Github-Store?label=Release&logo=github"/>
  </a>
  <a href="https://github.com/rainxchzed/Github-Store/stargazers">
    <img alt="GitHub Stars" src="https://img.shields.io/github/stars/rainxchzed/Github-Store?style=social"/>
  </a>
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Koin" src="https://img.shields.io/badge/DI-Koin-3C5A99?logo=kotlin&logoColor=white"/>
</p>

<p align="center">
GitHub Store 是一个面向 GitHub Releases 的跨平台 “Play Store”。  
它可以发现真正可安装的二进制文件仓库，并让你在一个地方完成
安装、跟踪和更新。
</p>

<p align="center">
  <img src="../screenshots/banner.png" />
</p>

---

### 所有截图可在 [screenshots/](screenshots/) 目录中查看

<img src="/screenshots/preview.gif" align="right" width="320"/>

## ✨ 什么是 GitHub Store？

GitHub Store 是一个使用 **Kotlin Multiplatform（Android + Desktop）**
构建的应用，它将 GitHub Releases 转换为类似应用商店的体验。

- 仅显示包含 **真实可安装资源** 的仓库  
  （APK、EXE、DMG、AppImage、DEB、RPM 等）。
- 自动识别你的平台并提供正确的安装文件。
- 始终安装最新发布版本，显示更新日志，
  并在 Android 上提供更新提醒。
- 提供美观的详情页，包含统计信息、README 和开发者信息。

---

## 🔃 下载

<a href="https://github.com/rainxchzed/Github-Store/releases">
   <image src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png" height="80"/>
 </a>

<a href="https://f-droid.org/en/packages/zed.rainxch.githubstore/">
   <image src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>

> [!IMPORTANT]
> 在 macOS 上，系统可能会提示 Apple 无法验证 GitHub Store 是否包含恶意软件。
> 这是因为应用并非通过 App Store 分发，且尚未进行公证。
> 你可以在“系统设置 → 隐私与安全性 → 仍要打开”中继续运行。

---

## 🚀 核心功能

- **智能发现**
    - 首页提供 “Trending / Recently Updated / New” 分类（基于时间的筛选）。
    - 仅显示包含有效安装文件的仓库。
    - 平台感知排序，为 Android / Desktop 用户优先展示相关应用。

- **安装最新版本**
    - 使用 `/releases/latest` 获取每个仓库的最新版本。
    - 仅展示最新发布中的安装资源。
    - 提供一个 “Install latest” 按钮，以及该版本的全部可下载文件。

- **丰富的详情页**
    - 应用名称、版本、“Install latest” 按钮。
    - Star 数、Fork 数、未关闭 Issue 数。
    - 渲染后的 README（作为应用介绍）。
    - 支持 Markdown 的最新发布说明。
    - 带平台标识和文件大小的安装文件列表。

- **跨平台体验**
    - Android：打开系统安装器安装 APK，
      在本地数据库中跟踪安装状态，并提示更新。
    - Desktop（Windows / macOS / Linux）：
      将安装文件保存到下载目录，并使用系统默认程序打开。

- **外观与主题**
    - 全平台采用 **Material 3 Expressive** 设计。
    - Android 支持 Material You 动态配色（如可用）。
    - 可选 AMOLED 黑色模式，适合 OLED 屏幕。

- **安全与检查（Android）**
    - 可选 GitHub OAuth（设备码流程）登录，用于提高 API 限额。
    - 安装前可通过 “Open in AppManager”
      检查 APK 的权限与追踪器。

---

## 🔍 我的应用如何出现在 GitHub Store 中？

GitHub Store **不使用私有索引或人工审核**。  
只要满足以下条件，项目就有机会被自动收录：

1. **公开 GitHub 仓库**
    - 仓库必须为 `public`。

2. **至少 1 个已发布的 Release**
    - 必须通过 GitHub Releases 创建（仅打 tag 不够）。
    - 最新 Release 不能是 draft 或 prerelease。

3. **最新 Release 包含可安装资源**
    - 至少包含一个支持的文件类型：
        - Android：`.apk`
        - Windows：`.exe`, `.msi`
        - macOS：`.dmg`, `.pkg`
        - Linux：`.deb`, `.rpm`, `.AppImage`
    - GitHub 自动生成的源码压缩包会被忽略。

4. **可被搜索 / 主题发现**
    - 使用 GitHub 公共 Search API 搜索仓库。
    - 主题、语言和描述会影响排序。
    - Star 数达到一定水平可提高可见性。

---

## 🧭 GitHub Store 的工作原理（简述）

1. **搜索**
    - 使用平台感知查询调用 `/search/repositories`。
    - 基于主题、语言和描述进行简单评分。
    - 排除已归档的仓库。

2. **Release 与资源检查**
    - 调用 `/repos/{owner}/{repo}/releases/latest`。
    - 检查是否存在适合当前平台的安装文件。
    - 若不存在，则从结果中移除。

3. **详情页**
    - 仓库信息：名称、作者、描述、Stars、Forks、Issues。
    - 最新 Release：标签、发布时间、更新日志、资源文件。
    - 主分支 README 作为“应用信息”展示。

4. **安装流程**
    - 点击 “Install latest”：
        - 选择最适合当前平台的资源。
        - 下载文件。
        - 交由系统安装程序处理。
        - Android 上将安装信息保存到本地数据库。

---

## ⚙️ 技术栈

- **最低 Android SDK：24**

- **语言与平台**
    - Kotlin Multiplatform（Android + JVM Desktop）
    - Compose Multiplatform UI（Material 3）

- **异步与状态管理**
    - Kotlin Coroutines + Flow
    - AndroidX Lifecycle

- **网络与数据**
    - Ktor 3
    - Kotlinx Serialization JSON
    - Kotlinx Datetime
    - Room + KSP（Android）

- **依赖注入**
    - Koin 4

- **导航**
    - JetBrains Navigation Compose

- **认证与安全**
    - GitHub OAuth（设备码流程）
    - Androidx DataStore

- **媒体与 Markdown**
    - Coil 3
    - multiplatform-markdown-renderer-m3

- **日志与工具**
    - Kermit
    - Compose Hot Reload
    - ProGuard / R8

---

## ✅ 为什么选择 GitHub Store？

- **无需手动寻找 GitHub Releases**
- **跟踪已安装的应用**
- **始终安装最新版本**
- **Android 与 Desktop 一致的体验**
- **完全开源，可扩展**

---

## ⚠️ 免责声明

GitHub Store 仅用于帮助发现和下载
由第三方开发者发布在 GitHub 上的 Release 资源。

使用 GitHub Store 即表示你同意：
所有下载的软件均由你自行承担安装和运行风险。

---

## 📄 许可证

GitHub Store 使用 **Apache License, Version 2.0** 进行分发。

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