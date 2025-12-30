<div align="center">
<img src="https://github.com/rainxchzed/Github-Store/blob/main/composeApp/src/commonMain/composeResources/drawable/app-icon.png" width="200" alt="プロジェクトのロゴ"/>
</div>

<h1 align="center">GitHub Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="ライセンス" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"/></a>
  <a href="#"><img alt="プラットフォーム" src="https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop-brightgreen"/></a>
  <a href="https://github.com/rainxchzed/Github-Store/releases">
    <img alt="リリース" src="https://img.shields.io/github/v/release/rainxchzed/Github-Store?label=Release&logo=github"/>
  </a>
  <a href="https://github.com/rainxchzed/Github-Store/stargazers">
    <img alt="GitHub スター" src="https://img.shields.io/github/stars/rainxchzed/Github-Store?style=social"/>
  </a>
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Koin" src="https://img.shields.io/badge/DI-Koin-3C5A99?logo=kotlin&logoColor=white"/>
</p>

<p align="center">
GitHub Store は、GitHub のリリース向けのクロスプラットフォーム版「Play ストア」です。
実際にインストール可能なバイナリを提供するリポジトリを見つけ、
1 か所からインストール・追跡・更新を行えます。
</p>

<p align="center">
  <img src="../screenshots/banner.png" />
</p>

---

### すべてのスクリーンショットは [screenshots/](screenshots/) フォルダにあります。

<img src="/screenshots/preview.gif" align="right" width="320"/>

## ✨ GitHub Store とは？

GitHub Store は Kotlin Multiplatform（Android + Desktop）で作られたアプリで、
GitHub Releases をアプリストアのような体験に変換します。

- 実際にインストール可能なアセット（APK、EXE、DMG、AppImage、DEB、RPM など）を提供する
  リポジトリのみを表示します。
- 使用中のプラットフォームを検出し、適切なインストーラを表示します。
- 常に最新の公開リリースをインストールし、変更履歴を表示。
  Android では更新通知も可能です。
- 統計情報、README、開発者情報を含む洗練された詳細画面を提供します。

---

## 🔃 ダウンロード

<a href="https://github.com/rainxchzed/Github-Store/releases">
   <image src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png" height="80"/>
 </a>

<a href="https://f-droid.org/en/packages/zed.rainxch.githubstore/">
   <image src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>

> [!IMPORTANT]
> macOS では、Apple が GitHub Store を検証できないという警告が表示される場合があります。
> これは App Store 外で配布され、まだノータライズされていないためです。
> 「システム設定 → プライバシーとセキュリティ → このまま開く」から許可できます。

---

## 🚀 機能

- **スマートな発見**
    - 「Trending」「Recently Updated」「New」セクションを時間ベースで表示。
    - 有効なインストール可能アセットを持つリポジトリのみを表示。
    - プラットフォームを考慮したランキングで、関連性の高いアプリを優先表示。

- **最新リリースのインストール**
    - 各リポジトリに対して `/releases/latest` を使用。
    - 最新リリースのアセットのみを表示。
    - 「Install latest」1 回の操作と、利用可能なインストーラ一覧。

- **充実した詳細画面**
    - アプリ名、バージョン、「Install latest」ボタン。
    - スター数、フォーク数、未解決 Issue。
    - レンダリングされた README（このアプリについて）。
    - Markdown 対応の最新リリースノート。
    - プラットフォーム別・ファイルサイズ付きのインストーラ一覧。

- **クロスプラットフォーム UX**
    - Android：APK をシステムインストーラで開き、ローカル DB で管理。
    - Desktop（Windows / macOS / Linux）：
      ダウンロードフォルダに保存し、既定のアプリで開きます。

- **外観とテーマ**
    - 全プラットフォームで **Material 3 Expressive** を使用。
    - Android では Material You のダイナミックカラーに対応。
    - OLED デバイス向けの AMOLED ブラックモード（任意）。

- **安全性と検査（Android）**
    - API 制限を緩和するための GitHub OAuth（Device Flow）ログイン（任意）。
    - インストール前に APK の権限やトラッカーを確認できる
      「Open in AppManager」アクション。

---

## 🔍 アプリが GitHub Store に表示される条件

GitHub Store は、非公開インデックスや手動キュレーションを行いません。  
以下の条件を満たすと自動的に表示されます。

1. **GitHub 上の公開リポジトリ**
    - 可視性は `public` である必要があります。

2. **少なくとも 1 つの公開リリース**
    - GitHub Releases で作成されていること（タグのみは不可）。
    - 最新リリースがドラフトやプレリリースでないこと。

3. **最新リリースにインストール可能なアセットが存在**
    - 対応拡張子を含むファイルが少なくとも 1 つ必要です：
        - Android：`.apk`
        - Windows：`.exe`, `.msi`
        - macOS：`.dmg`, `.pkg`
        - Linux：`.deb`, `.rpm`, `.AppImage`
    - GitHub が自動生成するソースコードアーカイブは無視されます。

4. **検索 / トピックで発見可能**
    - GitHub Search API を使用して取得されます。
    - トピック、言語、説明がランキングに影響します。
    - いくつかのスターがあると表示されやすくなります。

---

## 🧭 GitHub Store の仕組み（概要）

1. **検索**
    - プラットフォームに応じた `/search/repositories` クエリを使用。
    - トピック・言語・説明に基づく簡易スコアリング。
    - アーカイブ済みリポジトリは除外。

2. **リリースとアセットの確認**
    - `/repos/{owner}/{repo}/releases/latest` を呼び出し。
    - 対応アセットが存在するかをチェック。
    - 見つからない場合は結果から除外。

3. **詳細画面**
    - リポジトリ情報、スター、フォーク、Issue。
    - 最新リリースのタグ、日付、変更履歴、アセット。
    - README を「このアプリについて」として表示。

4. **インストールフロー**
    - 「Install latest」をタップすると：
        - 最適なアセットを選択。
        - ダウンロード。
        - OS のインストーラへ委譲。
        - Android ではローカル DB に記録。

---

## ⚙️ 技術スタック

- **最小 Android SDK：24**

- **言語・プラットフォーム**
    - Kotlin Multiplatform（Android + JVM Desktop）
    - Compose Multiplatform UI（Material 3）

- **非同期・状態管理**
    - Kotlin Coroutines + Flow
    - AndroidX Lifecycle

- **ネットワーク・データ**
    - Ktor 3
    - Kotlinx Serialization JSON
    - Kotlinx Datetime
    - Room + KSP（Android）

- **DI**
    - Koin 4

- **ナビゲーション**
    - JetBrains Navigation Compose

- **認証・セキュリティ**
    - GitHub OAuth（Device Code Flow）
    - Androidx DataStore

- **メディア・Markdown**
    - Coil 3
    - multiplatform-markdown-renderer-m3

- **ログ・ツール**
    - Kermit
    - Compose Hot Reload
    - ProGuard / R8

---

## ✅ GitHub Store を使う理由

- **リリース探しに時間を使わなくて済む**
- **インストール済みアプリの管理**
- **常に最新バージョン**
- **Android と Desktop で統一された体験**
- **オープンソースで拡張しやすい**

---

## ⚠️ 免責事項

GitHub Store は、第三者開発者が GitHub に公開した
リリースアセットの発見とダウンロードを支援するだけです。

本アプリを使用することで、ダウンロードしたソフトウェアを
自己責任でインストール・実行することに同意したものとみなされます。

---

## 📄 ライセンス

GitHub Store は **Apache License, Version 2.0** のもとで公開されています。

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