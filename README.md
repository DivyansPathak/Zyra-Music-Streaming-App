<p align="center"> <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.9.20-7F52FF?logo=kotlin&logoColor=white"/> <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack_Compose-1.6-4285F4?logo=jetpackcompose&logoColor=white"/> <img alt="Supabase" src="https://img.shields.io/badge/Supabase-1.162.4-3ECF8E?logo=supabase&logoColor=white"/> <img alt="Koin" src="https://img.shields.io/badge/Koin-3.5.0-5B2F91?logo=koin&logoColor=white"/> <img alt="License" src="https://img.shields.io/badge/License-MIT-blue.svg"/> </p>

# Zyra Music Player

A modern Android music player built with Jetpack Compose, offering seamless music playback, personalized playlists, and a rich user experience.

## Project Structure

```
.
├── music/zyra/
│   ├── MainActivity.kt               # Main entry point for the Android application
│   ├── MainApplication.kt            # Application class for global setup (Koin, NewPipe)
│   ├── data/                         # Data layer (local/remote sources, mappers, DTOs, Supabase client)
│   │   ├── local/                    # Room Database setup and DAOs
│   │   ├── mapper/                   # Data mapping between DTOs, Entities, and Domain models
│   │   ├── remote/                   # Ktor HTTP client, remote data source interface/implementation, Supabase
│   │   └── utils/                    # Constants (API URLs, DB names)
│   ├── di/                           # Dependency Injection (Koin modules)
│   ├── domain/                       # Business logic (models, repositories interfaces, use cases, error handling)
│   │   ├── model/
│   │   ├── repository/
│   │   └── utils/
│   ├── exoplayer/                    # Media3 ExoPlayer integration, MusicService, Queue Management
│   │   └── utils/
│   ├── navigation/                   # Jetpack Compose Navigation setup and route definitions
│   └── presentation/                 # UI layer (Screens, ViewModels, Composable components)
│       ├── addPlaylist/
│       ├── common/
│       ├── home/
│       ├── libraryScreen/
│       ├── login/
│       ├── newPlayer/
│       ├── playlistScreen/
│       ├── profileScreen/
│       └── searchScreen/
```

## Supported Platforms

*   **Android**: API 21+

## Key Features

*   **Modern UI**: Built with Jetpack Compose for a fluid and responsive experience.
*   **Seamless Music Playback**: Powered by Media3 ExoPlayer for reliable audio streaming.
*   **Intelligent Queue Management**: Dynamically manages playback queues, including "Up Next" suggestions.
*   **Personalized Playlists**: Create, manage, and favorite songs into custom playlists.
*   **Search Functionality**: Discover new music with powerful search and suggestions (including YouTube).
*   **User Authentication**: Securely manage user accounts via Supabase (Google and Email/Password).
*   **Local Caching**: Offline support for playlists and metadata using Room Database.
*   **Sleep Timer**: Automatically pause playback after a set duration or at the end of the current track.
*   **Playback Controls**: Comprehensive controls including shuffle, repeat, seek, play/pause.
*   **Dynamic UI Theming**: Adapts UI colors based on album art for an immersive experience.

## Technologies Used

*   **Kotlin**: Primary programming language.
*   **Jetpack Compose**: Modern Android UI toolkit.
*   **Media3 ExoPlayer**: Robust media playback library.
*   **Ktor Client**: For efficient network requests to various music APIs.
*   **Supabase**: Backend-as-a-Service for authentication, user data, and dynamic content.
*   **Room Persistence Library**: For local database caching.
*   **NewPipe Extractor**: Extracts stream URLs from various sources.
*   **Koin**: Lightweight dependency injection framework.
*   **Coil**: Image loading library for Compose.
*   **Coroutines & Flow**: For asynchronous programming and reactive data streams.
*   **Material Design 3**: Modern UI/UX components.
*   **ConstraintLayout (Compose)**: Flexible UI positioning.

## Installation & Usage

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/zyra-music-player.git
    cd zyra-music-player
    ```

2.  **Open in Android Studio**:
    Open the project in Android Studio (Jellyfish | 2023.3.1 or newer recommended). Let Gradle sync.

3.  **Supabase Configuration**:
    Zyra Music uses Supabase for user authentication and managing personal playlists. You need to configure your Supabase project keys.
    Create a `local.properties` file in the root of your project (if it doesn't exist) and add the following:
    ```properties
    SUPABASE_URL=""
    SUPABASE_ANON_KEY=""
    ```
    Also, add your Google Sign-In `client_id` for Google Authentication. Create `app/src/main/res/values/strings.xml` if it doesn't exist or add to an existing one:
    ```xml
    <!-- In app/src/main/res/values/strings.xml -->
    <string name="google_sign_in_client_id">YOUR_WEB_CLIENT_ID_FOR_GOOGLE_SIGN_IN</string>
    ```
    Replace `YOUR_WEB_CLIENT_ID_FOR_GOOGLE_SIGN_IN` with your Web Client ID from the Google Cloud Console (used for backend server authentication).

4.  **Build and Run**:
    *   Connect an Android device or start an emulator.
    *   Click the 'Run' button in Android Studio.
    *   The application should build and deploy to your selected device/emulator.

## Deployment

To create a production-ready APK or AAB:

1.  Go to `Build` -> `Generate Signed Bundle / APK...` in Android Studio.
2.  Follow the prompts to create a signed APK or Android App Bundle.
