<div align="center">
<img src="https://github.com/rainxchzed/Github-Store/blob/main/composeApp/src/commonMain/composeResources/drawable/app-icon.png" width="200" alt="Logo du projet"/>
</div>

<h1 align="center">GitHub Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="Licence" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"/></a>
  <a href="#"><img alt="Plateformes" src="https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop-brightgreen"/></a>
  <a href="https://github.com/rainxchzed/Github-Store/releases">
    <img alt="Version" src="https://img.shields.io/github/v/release/rainxchzed/Github-Store?label=Release&logo=github"/>
  </a>
  <a href="https://github.com/rainxchzed/Github-Store/stargazers">
    <img alt="Étoiles GitHub" src="https://img.shields.io/github/stars/rainxchzed/Github-Store?style=social"/>
  </a>
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Koin" src="https://img.shields.io/badge/DI-Koin-3C5A99?logo=kotlin&logoColor=white"/>
</p>

<p align="center">
GitHub Store est un « Play Store » multiplateforme pour les releases GitHub.
Il découvre des dépôts proposant de vrais binaires installables et vous permet
de les installer, suivre et mettre à jour depuis un seul endroit.
</p>

<p align="center">
  <img src="../screenshots/banner.png" />
</p>

---

### Toutes les captures d’écran sont disponibles dans le dossier [screenshots/](screenshots/).

<img src="/screenshots/preview.gif" align="right" width="320"/>

## ✨ Qu’est-ce que GitHub Store ?

GitHub Store est une application Kotlin Multiplatform (Android + Desktop)
qui transforme les GitHub Releases en une expérience fluide de type app store :

- Affiche uniquement les dépôts qui fournissent réellement des fichiers installables
  (APK, EXE, DMG, AppImage, DEB, RPM, etc.).
- Détecte automatiquement votre plateforme et propose l’installateur approprié.
- Installe toujours la dernière version publiée, affiche le changelog et peut notifier
  des mises à jour pour les applications installées (sur Android).
- Propose un écran de détails soigné avec statistiques, README et informations développeur.

---

## 🔃 Téléchargement

<a href="https://github.com/rainxchzed/Github-Store/releases">
   <image src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png" height="80"/>
 </a>

<a href="https://f-droid.org/en/packages/zed.rainxch.githubstore/">
   <image src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>

> [!IMPORTANT]
> Sur macOS, un avertissement peut indiquer qu’Apple ne peut pas vérifier que GitHub Store
> est exempt de logiciels malveillants. Cela se produit car l’application est distribuée
> en dehors de l’App Store et n’est pas encore notarialisée.
> Vous pouvez l’autoriser via
> System Settings → Privacy & Security → Open Anyway.

---

## 🚀 Fonctionnalités

- **Découverte intelligente**
    - Sections d’accueil « Trending », « Recently Updated » et « New » avec filtres temporels.
    - Seuls les dépôts avec des fichiers installables valides sont affichés.
    - Classement tenant compte de la plateforme pour afficher les apps pertinentes en priorité.

- **Installation de la dernière release**
    - Utilise `/releases/latest` pour chaque dépôt.
    - Affiche uniquement les fichiers de la dernière version.
    - Une action unique « Install latest » avec une liste extensible des installateurs disponibles.

- **Écran de détails riche**
    - Nom de l’application, version, bouton « Install latest ».
    - Étoiles, forks et issues ouvertes.
    - README rendu (« À propos de cette application »).
    - Notes de version récentes avec support Markdown.
    - Liste des installateurs avec plateforme et taille des fichiers.

- **UX multiplateforme**
    - Android : ouvre les APK avec l’installateur système, suit les installations dans une base locale
      et affiche les mises à jour disponibles.
    - Desktop (Windows/macOS/Linux) : télécharge les installateurs dans le dossier Téléchargements
      et les ouvre avec le gestionnaire par défaut.

- **Apparence et thèmes**
    - Design Material 3 avec composants **Material 3 Expressive** sur toutes les plateformes.
    - Couleurs dynamiques Material You sur Android (si disponibles).
    - Mode noir AMOLED optionnel pour les écrans OLED.

- **Sécurité et inspection (Android)**
    - Connexion GitHub OAuth optionnelle (Device Flow) pour augmenter les limites API.
    - Action « Open in AppManager » pour inspecter permissions et trackers avant installation.

---

## 🔍 Comment mon application apparaît-elle dans GitHub Store ?

GitHub Store n’utilise aucune indexation privée ni curation manuelle.  
Votre projet peut apparaître automatiquement s’il respecte les conditions suivantes :

1. **Dépôt public sur GitHub**
    - La visibilité doit être `public`.

2. **Au moins une release publiée**
    - Créée via GitHub Releases (pas seulement un tag).
    - La dernière release ne doit pas être un brouillon ni une préversion.

3. **Fichiers installables dans la dernière release**
    - Au moins un fichier avec une extension supportée :
        - Android : `.apk`
        - Windows : `.exe`, `.msi`
        - macOS : `.dmg`, `.pkg`
        - Linux : `.deb`, `.rpm`, `.AppImage`
    - Les archives de code source générées automatiquement par GitHub sont ignorées.

4. **Découvrable via la recherche / les topics**
    - Les dépôts sont récupérés via l’API publique GitHub Search.
    - Les topics, le langage et la description influencent le classement.
    - Avoir quelques étoiles augmente les chances d’apparaître dans les sections populaires.

Si ces conditions sont remplies, GitHub Store peut trouver votre dépôt automatiquement,
sans soumission manuelle.

---

## 🧭 Fonctionnement de GitHub Store (vue d’ensemble)

1. **Recherche**
    - Utilise `/search/repositories` avec des requêtes adaptées à la plateforme.
    - Applique un score simple basé sur les topics, le langage et la description.
    - Exclut les dépôts archivés ou avec peu de signaux.

2. **Vérification des releases**
    - Appelle `/repos/{owner}/{repo}/releases/latest`.
    - Analyse les assets pour détecter les fichiers compatibles.
    - Exclut le dépôt si aucun fichier valide n’est trouvé.

3. **Écran de détails**
    - Informations du dépôt : nom, propriétaire, description, étoiles, forks, issues.
    - Dernière release : tag, date, changelog, fichiers.
    - README chargé depuis la branche par défaut.

4. **Installation**
    - Lors du clic sur « Install latest » :
        - Sélection du meilleur fichier pour la plateforme.
        - Téléchargement en streaming.
        - Lancement via l’installateur système.
        - Sur Android, suivi dans une base locale.

---

## ⚙️ Stack technique

- **SDK Android minimum : 24**

- **Langage et plateforme**
    - Kotlin Multiplatform (Android + JVM Desktop)
    - Compose Multiplatform UI (Material 3)

- **Asynchrone et état**
    - Kotlin Coroutines + Flow
    - AndroidX Lifecycle

- **Réseau et données**
    - Ktor 3
    - Kotlinx Serialization JSON
    - Kotlinx Datetime
    - Room + KSP (Android)

- **Injection de dépendances**
    - Koin 4

- **Navigation**
    - JetBrains Navigation Compose

- **Authentification et sécurité**
    - GitHub OAuth (Device Code Flow)
    - Androidx DataStore

- **Médias et Markdown**
    - Coil 3
    - multiplatform-markdown-renderer-m3

- **Logs et outils**
    - Kermit
    - Compose Hot Reload
    - ProGuard / R8

---

## ✅ Pourquoi utiliser GitHub Store ?

- **Fini la recherche manuelle dans les releases**
- **Suivi des applications installées**
- **Toujours la dernière version**
- **Expérience cohérente sur Android et Desktop**
- **Open source et extensible**

---

## ⚠️ Avertissement

GitHub Store aide uniquement à découvrir et télécharger des fichiers publiés
par des développeurs tiers sur GitHub.

En utilisant GitHub Store, vous acceptez d’installer et d’exécuter des logiciels
à vos propres risques.

---

## 📄 Licence

GitHub Store est distribué sous la **licence Apache, Version 2.0**.

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