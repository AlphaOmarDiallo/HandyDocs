# HandyDocs : Scan, OCR, and PDFs safe offline with AI-powered tools!
Turn your phone into a powerful document scanner with HandyDocs! Scan, extract text with AI-powered OCR, and save your documents as PDFs—all offline for secure access anytime.

<img src="https://play-lh.googleusercontent.com/PpzJcqKzuktemtwHwes5PDyN8a5pfT32o8N8_2Bqr40FqfBAtQ2n6F9uYBSDPoOeJg=w5120-h2880" width="200" /> <img src="https://play-lh.googleusercontent.com/m84-_s0SOrbSB_cA-BnCSWFuh3oJSiFszfSq8rxq6WjuMBJLWAjPBGexY99EQuIQiYc=w5120-h2880" width="200" /> <img src="https://play-lh.googleusercontent.com/oVjcdnebO5cA3hN_GBAhfmlMqdXeFRJRr-J-fB84dy4mFvIpw1xEbvxtbSkWQyPczgNc=w5120-h2880" width="200" /> <img src="https://play-lh.googleusercontent.com/9iDnuvo1AIu3TcFw3Q-IQWgrQ0--PojnVt-jffeGr6iTycCtaqRRoyBrqLftcbla3HE=w5120-h2880" width="200" />
<a href="https://play.google.com/store/apps/details?id=com.alphaomardiallo.handydocs" target="_blank">
<img src="https://raw.githubusercontent.com/pioug/google-play-badges/06ccd9252af1501613da2ca28eaffe31307a4e6d/svg/English.svg" alt="Get it on Google Play" width="200"/>
</a>

## Key Features
✅ AI Alt Text Generator – Automatically generate alt text for images using AI.

✅ OCR Scanner – Extract text from images, files, or your camera.

✅ PDF Creator – Scan documents and save them as high-quality PDFs.

✅ Offline Document Safe – Store and access scanned PDFs anytime, even without internet.

## How to install
1. Open an IDE compatible with Android. I recommend Android Studio, it is a free IDE provided by Jetbrains.
2. git clone https://github.com/AlphaOmarDiallo/HandyDocs.git
3. In the root you will need to create or update the following files : 
   1. local.default.properties with this variable : GEMINI_API_KEY=DEFAULT_API_KEY
   2. secrets.properties with this variable : GEMINI_API_KEY=YOUR_GEMINI_API_KEY
4. Build the project
5. Run the project

## Tech stack
### Core Android & UI
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system and components
- **Navigation Compose** - Navigation framework
- **Lifecycle Components** - Lifecycle-aware components
- **Core Splash Screen** - Splash screen API
- **Google Fonts for Compose** - Typography integration

### Camera & Image Processing
- **CameraX** - Camera API
- **ML Kit** - Document scanning and text recognition
   - Document scanner
   - Text recognition for multiple languages
- **Coil** - Image loading library
- **Zoomable Image** - Image zoom functionality

### Data & Storage
- **Room** - Database persistence
- **Gson** - JSON serialization/deserialization
- **KotlinX Serialization** - Kotlin serialization library

### Networking
- **Ktor Client** - HTTP client

### Dependency Injection
- **Koin** - Dependency injection framework

### Animations & UI Enhancement
- **Lottie** - Animation library

### Monitoring & Analytics
- **Timber** - Logging utility
- **Firebase Crashlytics** - Crash reporting

### Monetization
- **AdMob** - Mobile advertising

### Build Tools
- **Android Gradle Plugin**
- **KSP** - Kotlin Symbol Processing
- **Google Maps Secrets** - API key management

## Project Structure
### Root Files
- `README.md` - Project documentation
- `build.gradle.kts` - Project-level build configuration
- `settings.gradle.kts` - Project settings configuration
- `gradle.properties` - Gradle properties
- `local.properties` - Local environment configuration
- `local.defaults.properties` - Default local properties
- `secrets.properties` - API keys and sensitive data
- `gradlew`, `gradlew.bat` - Gradle wrapper executables
- `handykeystore` - App signing keystore

### Gradle Configuration
- `gradle/libs.versions.toml` - Dependency version management
- `gradle/wrapper/` - Gradle wrapper files

### App Module
- `app/build.gradle.kts` - App module build configuration
- `app/proguard-rules.pro` - ProGuard configuration
- `app/google-services.json` - Firebase configuration

#### Source Code
- `app/src/main/java/com/alphaomardiallo/handydocs/`
   - `HandyDocsApp.kt` - Application class
   - `di/` - Dependency injection
   - `common/` - Shared components
      - `data/` - Data layer (Database, Repositories)
      - `domain/` - Domain layer (Models, Repositories interfaces)
         - `destination/` - Navigation destinations
         - `model/` - Domain models
         - `navigator/` - Navigation system
         - `repository/` - Repository interfaces
      - `presentation/` - UI components
         - `base/` - Base classes for UI
         - `composable/` - Reusable Compose components
         - `main/` - Main activity and ViewModel
         - `model/` - UI models
         - `navigation/` - Navigation components
         - `theme/` - App theme (Colors, Typography)

#### Features
- `feature/altgenerator/` - AI Alt Text Generation feature
   - `data/` - Data layer (API, DataSource)
   - `domain/` - Domain layer (Models, UseCases)
   - `presentation/` - UI components
- `feature/docviewer/` - Document viewing feature
- `feature/ocr/` - Optical Character Recognition feature
   - `data/` - Data layer
   - `domain/` - Domain layer
   - `presentation/` - UI components
- `feature/pdfsafe/` - PDF Storage and Management feature

#### Resources
- `app/src/main/res/`
   - `drawable/` - Vector drawables and icons
   - `mipmap-*/` - App icons
   - `raw/` - Raw resources (animations)
   - `values/` - Strings, colors, themes
   - `values-*/` - Localized strings (ar, de, es, fr, hi)
   - `xml/` - XML configurations

#### Tests
- `app/src/androidTest/` - Instrumentation tests
- `app/src/test/` - Unit tests

#### Release Files
- `app/release/` - Release builds and profiles

## Contributing
1. Fork the repo
2. Create a new branch (feature/my-feature)
3. Commit your changes
4. Push and create a PR

## Licence
Copyright 2025 Alpha Omar DIALLO

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
