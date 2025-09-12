# Zyra Music Player

A modern Android music streaming application built with Jetpack Compose, ExoPlayer (Media3), and Supabase, offering a seamless and intuitive music playback experience with dynamic queue management and direct YouTube content integration.

## Project Structure

```
.
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/
│               └── com/zyra/music/zyra/
│                   ├── data/                     # Data sources, mappers, DTOs, Ktor HTTP client
│                   ├── di/                       # Koin Dependency Injection module
│                   ├── domain/                   # Business logic, models, repositories
│                   ├── exoplayer/                # Media3 (ExoPlayer) setup, music service, queue managers, caching
│                   ├── navigation/               # Jetpack Compose navigation routes and graph
│                   ├── presentation/             # UI layer (Jetpack Compose screens, ViewModels, UI components)
│                   │   ├── common/
│                   │   ├── home/
│                   │   ├── login/
│                   │   ├── newPlayer/            # Core swipeable player implementation
│                   │   ├── playerScreen/         # Legacy player screen
│                   │   ├── searchScreen/
│                   │   └── ui/theme/
│                   ├── MainActivity.kt           # Main entry point Activity
│                   └── MainApplication.kt        # Application class for global setup (Koin, NewPipe Extractor)
├── build.gradle.kts (app)
├── build.gradle.kts (project)
└── settings.gradle.kts
```

## Key Features

*   **Seamless Music Playback**: Powered by Media3 (ExoPlayer) with custom caching, dynamic adaptive streaming, and robust error handling.
*   **Dynamic Queue Management**: Effortlessly add songs, play next, reorder, and automatically fetch "Up Next" recommendations to keep the music flowing.
*   **Swipeable Mini/Full Player**: Enjoy an intuitive and fluid UI for switching between a compact mini-player and an immersive full-screen player experience.
*   **YouTube Content Integration**: Search and stream music directly from YouTube using the powerful `newpipe-extractor` library, bypassing traditional API limitations.
*   **Real-time Search**: Fast and responsive search functionality with instant suggestions as you type, leading to quicker discovery.
*   **User Authentication**: Secure user login and registration options powered by Supabase, including email/password and Google Sign-In.
*   **Modern UI/UX**: Crafted entirely with Jetpack Compose for a responsive, engaging, and visually appealing user interface.
*   **Dependency Injection**: Utilizes Koin for a clean, modular, and testable codebase.

## Screenshots

*(Include screenshots here to showcase the app's design and functionality)*

| Home Screen | Search Screen | Full Player |
| :---------: | :-----------: | :---------: |
| ![Home Screen Placeholder](https://via.placeholder.com/250x500?text=Home+Screen) | ![Search Screen Placeholder](https://via.placeholder.com/250x500?text=Search+Screen) | ![Full Player Placeholder](https://via.placeholder.com/250x500?text=Full+Player) |

## Technologies Used

*   **Kotlin**: The primary programming language for Android development.
*   **Jetpack Compose**: Google's modern toolkit for building native Android UI.
*   **Media3 (ExoPlayer)**: Robust and extensible media playback library.
*   **Ktor Client**: Asynchronous HTTP client for making network requests.
*   **Kotlinx Serialization**: Multiplatform JSON serialization/deserialization library.
*   **Koin**: Lightweight dependency injection framework for Kotlin.
*   **Supabase**: Open-source Firebase alternative used for user authentication.
*   **NewPipe Extractor**: A powerful library for extracting information from YouTube, SoundCloud, and other services.
*   **Android Navigation Compose**: For managing in-app navigation.
*   **Coil3**: Fast, lightweight image loading library for Android.
*   **Lottie**: For high-quality animations in the app.

## Installation & Usage

To get Zyra Music Player up and running on your local machine:

### Prerequisites

*   Android Studio Jellyfish | 2023.3.1 or newer
*   Android SDK 34
*   A physical Android device or emulator running Android 8.0 (API level 26) or higher.
*   Git

### Setup Steps

1.  **Clone the Repository:**

    ```bash
    git clone https://github.com/your-username/zyra-music-player.git
    cd zyra-music-player
    ```

2.  **Open in Android Studio:**
    Open the cloned project in Android Studio. Let it sync all Gradle dependencies.

3.  **Configure Environment Variables:**
    Create a `local.properties` file in the root of your project (if it doesn't exist) and add your sensitive keys. This file is excluded by Git.

    ```properties
    # For Supabase Backend
    SUPABASE_URL="https://kjegpczagrbhbgtubhxa.supabase.co"
    SUPABASE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtqZWdwY3phZ3JiaGJndHViaHhhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTU0OTk3NTMsImV4cCI6MjA3MTA3NTc1M30.-9RrnlOP3Vm1u_-0Wfdno5sPA91GcNryUVx4oVRqn3A"

    # For Google Sign-In (replace with your actual client ID)
    GOOGLE_SIGN_IN_CLIENT_ID="YOUR_GOOGLE_SIGN_IN_WEB_CLIENT_ID"

    # For Local YT Proxy (if used, adjust IP if necessary)
    YT_BASE_URL="http://192.168.1.37:8000"
    ```
    *   **Note on `GOOGLE_SIGN_IN_CLIENT_ID`**: This should be your Web (Server) Client ID obtained from the Google Cloud Console for OAuth 2.0.
    *   **Note on `YT_BASE_URL`**: The current value points to a local IP address. If you are running a custom YouTube proxy/backend service, ensure it's accessible from your device/emulator, or update this URL accordingly.

4.  **Build and Run:**
    *   Select your desired Android device or emulator from the Android Studio toolbar.
    *   Click the `Run 'app'` button (green triangle icon) to install and launch the application.

## Deployment

This project builds into a standard Android APK. To deploy to the Google Play Store or distribute, follow these steps:

1.  **Generate a Signed APK/App Bundle**:
    *   In Android Studio, navigate to `Build` > `Generate Signed Bundle / APK...`.
    *   Follow the on-screen prompts to create a new keystore or use an existing one.
    *   Select `release` as the build variant and choose `APK` or `Android App Bundle`.
2.  **Google Play Console**:
    *   Upload the generated App Bundle to the Google Play Console for distribution.

## Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature-name`).
3.  Make your changes and commit them (`git commit -m 'Add new feature'`).
4.  Push to the branch (`git push origin feature/your-feature-name`).
5.  Open a Pull Request.

Please ensure your code adheres to Kotlin/Android best practices and includes appropriate tests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.