# GeoHunt

GeoHunt is an interactive geolocation-based Android application that gamifies real-world exploration. Users can explore physical locations, collect points, compete with friends, and unlock items in a virtual shop.

This application was developed as a group project for **COMS 3090 at Iowa State University**.

## Features

- **Geolocation Tracking**: Real-time location tracking using Google Maps integration.
- **Interactive Gameplay**: Earn points by visiting specific geofenced locations.
- **Social Integration**: Add friends, view profiles, and compete on leaderboards.
- **Shop System**: Spend earned points on virtual items and customization.
- **User Statistics**: Track your progress with detailed stats on points, matches, and exploration.
- **Commenting & Rating**: Leave reviews and ratings for locations you've visited.

## Technology Stack

### Frontend (Android)
- **Language**: Java / Kotlin
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Key Libraries**:
  - `Google Maps SDK`: For map rendering and location services.
  - `Volley`: For handling network requests.
  - `Glide`: For efficient image loading and caching.
  - `Konfetti`: For particle systems and celebration effects.
  - `Android Image Cropper`: For profile picture customization.

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: 
  - MySQL (Production)
  - H2 (Testing/Dev)
- **Key Technologies**:
  - `Spring Data JPA`: For database interactions.
  - `Lombok`: To reduce boilerplate code.
  - `WebSocket`: For real-time features.
  - `Swagger/SpringDoc`: For API documentation.

## Project Structure

```
├── Frontend/
│   └── geohunt/          # Android Application Source
│       ├── app/          # Main App Module
│       └── build.gradle  # Build Configuration
├── Backend/
│   └── geolocation/      # Backend Service Source
│       └── Backend/      # Spring Boot Project Root
│           ├── src/      # Java Source Code
│           └── pom.xml   # Maven Dependencies
└── Documents/            # Project Documentation
```

---
*Developed by Alex Remiasz, Arjava Tripathi, Nathan Imig, and Evan Julson of Iowa State University.*
