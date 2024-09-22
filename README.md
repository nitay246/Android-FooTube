
---

# Android Video Management Application

This Android application is designed for comprehensive video content management, seamlessly integrated with a corresponding website. Users can upload, view, edit, and delete videos, all while ensuring secure interactions through JWT-based authentication. The app communicates with a backend server via RESTful APIs, providing a robust and responsive user experience.

## Technologies Utilized

- **Android SDK**: The foundational toolkit for developing Android applications.
- **Retrofit**: A type-safe HTTP client optimized for handling REST API interactions.
- **JWT (JSON Web Tokens)**: Ensures secure API communication by validating user authentication.
- **Room**: A powerful persistence library for managing local data storage.
- **ConstraintLayout**: Enables flexible and responsive user interface design.

## Prerequisites

- **Android Device or Emulator**: The application can be deployed on a physical Android device or an emulator.
- **Android Studio**: Download the latest version from [developer.android.com](https://developer.android.com/studio).
- **Backend Server**: Ensure the backend server is operational and configured with the necessary APIs for video management and user authentication.

## Core Features

- **User Authentication**: Secure login functionality leveraging JWT tokens.
- **Video Upload**: Allows users to upload video files directly to the server.
- **Video Management**: Users can view, edit, and delete their uploaded videos.
- **Profile Management**: Provides users with options to update their profile information.
- **Dark Mode Support**: Full compatibility with dark mode for a better user experience in low-light environments.
- **RecyclerView Integration**: Efficiently displays video lists with smooth and responsive scrolling.

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/your-android-app.git
cd your-android-app
```

### 2. Configure API Endpoints
- Open `Constants.java` and update the `BASE_URL` to point to your backend server's API.

### 3. Generate Fake Data
- Navigate to the backend server's root directory.
- Run the `seed.js` script to populate the database with fake data:
```bash
node seed.js
```

### 4. Build the Project
- Launch the project in Android Studio.
- Sync Gradle to ensure all dependencies are correctly installed.

### 5. Run the Application
- Connect an Android device or initiate an emulator.
- Click "Run" in Android Studio or use the shortcut Shift + F10.

## Local Data Storage

- **Room**: Handles local data storage efficiently, caching user data and video information to provide offline support and enhanced performance.

## Dark Mode Compatibility

- The application fully supports dark mode, automatically adapting UI elements according to the user's system preferences.

---

