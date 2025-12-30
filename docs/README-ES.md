<div align="center">
<img src="https://github.com/rainxchzed/Github-Store/blob/main/composeApp/src/commonMain/composeResources/drawable/app-icon.png" width="200" alt="Logotipo del proyecto"/>
</div>

<h1 align="center">GitHub Store</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="Licencia" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg"/></a>
  <a href="#"><img alt="Plataformas" src="https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop-brightgreen"/></a>
  <a href="https://github.com/rainxchzed/Github-Store/releases">
    <img alt="Lanzamiento" src="https://img.shields.io/github/v/release/rainxchzed/Github-Store?label=Release&logo=github"/>
  </a>
  <a href="https://github.com/rainxchzed/Github-Store/stargazers">
    <img alt="Estrellas de GitHub" src="https://img.shields.io/github/stars/rainxchzed/Github-Store?style=social"/>
  </a>
  <img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white"/>
  <img alt="Koin" src="https://img.shields.io/badge/DI-Koin-3C5A99?logo=kotlin&logoColor=white"/>
</p>

<p align="center">
GitHub Store es un «Play Store» multiplataforma para los releases de GitHub.
Descubre repositorios que ofrecen binarios realmente instalables y te permite
instalarlos, seguirlos y actualizarlos desde un solo lugar.
</p>

<p align="center">
  <img src="../screenshots/banner.png" />
</p>

---

### Todas las capturas de pantalla se pueden encontrar en la carpeta [screenshots/](screenshots/).

<img src="/screenshots/preview.gif" align="right" width="320"/>

## ✨ ¿Qué es GitHub Store?

GitHub Store es una aplicación Kotlin Multiplatform (Android + Desktop)
que convierte los GitHub Releases en una experiencia limpia al estilo de una tienda de aplicaciones:

- Muestra únicamente repositorios que realmente proporcionan archivos instalables
  (APK, EXE, DMG, AppImage, DEB, RPM, etc.).
- Detecta tu plataforma y muestra el instalador correcto.
- Siempre instala la última versión publicada, resalta el changelog y puede notificar
  sobre actualizaciones de las apps instaladas (en Android).
- Presenta una pantalla de detalles cuidada con estadísticas, README e información del desarrollador.

---

## 🔃 Descarga

<a href="https://github.com/rainxchzed/Github-Store/releases">
   <image src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png" height="80"/>
 </a>

<a href="https://f-droid.org/en/packages/zed.rainxch.githubstore/">
   <image src="https://f-droid.org/badge/get-it-on.png" height="80"/>
</a>

> [!IMPORTANT]
> En macOS, puede aparecer una advertencia indicando que Apple no puede verificar
> que GitHub Store esté libre de malware. Esto ocurre porque la aplicación se distribuye
> fuera del App Store y aún no está notarizada.
> Puedes permitir su ejecución en
> System Settings → Privacy & Security → Open Anyway.

---

## 🚀 Funcionalidades

- **Descubrimiento inteligente**
    - Secciones de inicio para «Trending», «Recently Updated» y «New» con filtros basados en tiempo.
    - Solo se muestran repositorios con archivos instalables válidos.
    - Clasificación consciente de la plataforma para mostrar primero las apps relevantes.

- **Instalación de la última release**
    - Usa `/releases/latest` para cada repositorio.
    - Muestra únicamente los archivos de la última versión.
    - Una acción única «Install latest» y una lista desplegable con todos los instaladores disponibles.

- **Pantalla de detalles rica**
    - Nombre de la app, versión y botón «Install latest».
    - Estrellas, forks e issues abiertos.
    - README renderizado («Acerca de esta app»).
    - Notas de la última versión con formato Markdown.
    - Lista de instaladores con plataforma y tamaño de archivo.

- **UX multiplataforma**
    - Android: abre APKs con el instalador del sistema, registra las instalaciones en una base de datos local
      y muestra indicadores de actualización.
    - Desktop (Windows/macOS/Linux): descarga los instaladores en la carpeta Descargas del usuario
      y los abre con el manejador predeterminado.

- **Apariencia y temas**
    - Diseño Material 3 con componentes **Material 3 Expressive** en todas las plataformas.
    - Soporte de colores dinámicos Material You en Android (cuando está disponible).
    - Modo negro AMOLED opcional para dispositivos OLED.

- **Seguridad e inspección (Android)**
    - Inicio de sesión opcional con GitHub OAuth (Device Flow) para aumentar los límites de la API.
    - Acción «Open in AppManager» para inspeccionar permisos y trackers antes de instalar.

---

## 🔍 ¿Cómo aparece mi aplicación en GitHub Store?

GitHub Store no utiliza indexación privada ni reglas de curación manual.  
Tu proyecto puede aparecer automáticamente si cumple las siguientes condiciones:

1. **Repositorio público en GitHub**
    - La visibilidad debe ser `public`.

2. **Al menos un release publicado**
    - Creado mediante GitHub Releases (no solo etiquetas).
    - El último release no debe ser un borrador ni una pre-release.

3. **Archivos instalables en el último release**
    - El último release debe contener al menos un archivo con una extensión compatible:
        - Android: `.apk`
        - Windows: `.exe`, `.msi`
        - macOS: `.dmg`, `.pkg`
        - Linux: `.deb`, `.rpm`, `.AppImage`
    - GitHub Store ignora los artefactos de código fuente generados automáticamente
      (`Source code (zip)` / `Source code (tar.gz)`).

4. **Descubrible mediante búsqueda / topics**
    - Los repositorios se obtienen a través de la API pública de búsqueda de GitHub.
    - Los topics, el lenguaje y la descripción influyen en la clasificación.
    - Tener algunas estrellas aumenta la probabilidad de aparecer en secciones populares.

Si tu repositorio cumple estas condiciones, GitHub Store puede encontrarlo automáticamente,
sin necesidad de envío manual.

---

## 🧭 Cómo funciona GitHub Store (visión general)

1. **Búsqueda**
    - Usa `/search/repositories` con consultas adaptadas a la plataforma.
    - Aplica una puntuación simple basada en topics, lenguaje y descripción.
    - Filtra repositorios archivados o con pocas señales.

2. **Comprobación de releases y archivos**
    - Llama a `/repos/{owner}/{repo}/releases/latest`.
    - Revisa el array `assets` para detectar archivos compatibles con la plataforma.
    - Si no se encuentra ningún archivo adecuado, el repositorio se excluye.

3. **Pantalla de detalles**
    - Información del repositorio: nombre, propietario, descripción, estrellas, forks, issues.
    - Último release: tag, fecha de publicación, changelog y archivos.
    - README cargado desde la rama principal y mostrado como «Acerca de esta app».

4. **Proceso de instalación**
    - Al tocar «Install latest»:
        - Se selecciona el archivo más adecuado para la plataforma actual.
        - Se descarga en streaming.
        - Se delega al instalador del sistema.
        - En Android, la instalación se registra en una base de datos local.

---

## ⚙️ Stack tecnológico

- **SDK mínimo de Android: 24**

- **Lenguaje y plataforma**
    - Kotlin Multiplatform (Android + JVM Desktop)
    - Compose Multiplatform UI (Material 3)

- **Asincronía y estado**
    - Kotlin Coroutines + Flow
    - AndroidX Lifecycle

- **Red y datos**
    - Ktor 3
    - Kotlinx Serialization JSON
    - Kotlinx Datetime
    - Room + KSP (Android)

- **Inyección de dependencias**
    - Koin 4

- **Navegación**
    - JetBrains Navigation Compose

- **Autenticación y seguridad**
    - GitHub OAuth (Device Code Flow)
    - Androidx DataStore

- **Medios y Markdown**
    - Coil 3
    - multiplatform-markdown-renderer-m3

- **Logs y herramientas**
    - Kermit
    - Compose Hot Reload
    - ProGuard / R8

---

## ✅ ¿Por qué usar GitHub Store?

- **No más búsqueda manual en los releases**
- **Seguimiento de aplicaciones instaladas**
- **Siempre la última versión**
- **Experiencia uniforme en Android y Desktop**
- **Open source y extensible**

---

## ⚠️ Descargo de responsabilidad

GitHub Store solo ayuda a descubrir y descargar archivos de releases ya publicados
en GitHub por desarrolladores externos.

Al usar GitHub Store, entiendes y aceptas que instalas y ejecutas software
bajo tu propia responsabilidad.

---

## 📄 Licencia

GitHub Store se distribuye bajo la **Licencia Apache, Versión 2.0**.

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