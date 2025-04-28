
# Kore: A Productivity App for Sustainable Living

## Overview
Kore is an AI-powered productivity application that helps users align daily activities with long-term goals through a personal economy system while promoting sustainable living practices. The app features a clean, modern interface built with Compose Multiplatform and is designed for desktop environments.

## Key Features

### 1. Personal Economy System
- **Habit-Based Points**: Earn points for completing habits (e.g., "Exercise" = 50 points)
- **Rewards Redemption**: Spend points on personalized rewards
- **Chain Reaction Rewards**: Bonus points for completing multiple habits in a day
- **Progressive Load System**: Points increase as habits improve
- **Streak Tracking**: Monitor consecutive days of habit completion

### 2. AI-Powered Insights
- **Personalized Feedback**: Analysis of goals and activity patterns
- **Habit Completion Predictions**: Visual predictions of habit completion likelihood
- **Adaptive Suggestions**: Tailored recommendations to align actions with objectives
- **Privacy-First Approach**: Uses local data processing with Gemini API integration

### 3. "Know Thyself" Onboarding Wizard
- **Guided Setup**: Step-by-step process for defining goals, habits, and rewards
- **Eco-Friendly Options**: Suggestions for green habits
- **User-Centric Design**: Intuitive setup process

### 4. Eco-Friendly Focus
- **Green Habits**: Tracking of eco-friendly activities with bonus points
- **Environmental Impact**: Highlights eco-friendly habit completion
- **Earth Day Theme**: Green-themed UI (primary color: #4CAF50)

### 5. Insights Dashboard
- **Progress Visualization**: Charts and statistics for points, habits, and streaks
- **Activity Tracking**: Record of completed habits and earned points
- **Goal Progress**: Visual representation of progress toward goals
- **Motivational Feedback**: Encouraging messages to maintain engagement

## Technical Stack
- **Kotlin Multiplatform**: Cross-platform development
- **Compose Multiplatform**: Modern UI toolkit
- **SQLDelight**: Type-safe SQL for database operations
- **Ktor**: Networking and API communication
- **Kotlinx Coroutines**: Asynchronous programming
- **Kotlinx Serialization**: JSON serialization/deserialization
- **Material 3**: Modern design system

## Getting Started

### Prerequisites
- JDK 17 or higher
- Gradle 8.0 or higher

### Building the Project
1. Clone the repository
2. Open the project in your IDE
3. Build the project using Gradle:
   ```
   gradlew build
   ```

### Running the Application
Run the application using Gradle:
```
gradlew run
```

### Creating Distributable Packages
The application can be packaged for distribution on Windows, macOS, and Linux:
```
gradlew packageDistributionForCurrentOS
```

This will create:
- `.msi` installer for Windows
- `.dmg` image for macOS
- `.deb` package for Linux

## Project Structure
- `composeApp/`: Contains the main application code
  - `src/commonMain/`: Shared code for all platforms
    - `kotlin/`: Kotlin source files
    - `composeResources/`: Compose resources
    - `sqldelight/`: Database schema definitions
  - `src/desktopMain/`: Desktop-specific code
    - `kotlin/`: Desktop implementation
    - `resources/`: Desktop resources
- `gradle/`: Gradle configuration files
- `build.gradle.kts`: Main build configuration
- `composeApp/build.gradle.kts`: App-specific build configuration

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
