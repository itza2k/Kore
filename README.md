
## 🌟Why Kore?🌱

In a world of endless distractions, Kore helps you stay focused on what truly matters. By combining behavioral science, gamification, and AI-powered insights, Kore creates a personal economy system that rewards meaningful actions and sustainable living.

Unlike traditional productivity apps that focus solely on task management, Kore connects your daily habits to your long-term goals, creating a seamless bridge between your present actions and future aspirations.

## 🔑 Key Features

### 💰 Personal Economy System

Transform your productivity into a rewarding personal economy:

- **Habit-Based Points**: Earn points for completing habits (e.g., "Exercise" = 50 points)
- **Rewards Marketplace**: Redeem points for personalized rewards you define
- **Chain Reaction Bonuses**: Earn multipliers for completing related habits
- **Progressive Load System**: Points increase as habits improve, keeping challenges fresh
- **Streak Tracking**: Build momentum with visual streak counters

### 🤖 AI-Powered Insights

Leverage the power of AI to optimize your productivity:

- **Personalized Feedback**: Receive analysis of your goals and activity patterns
- **Habit Completion Predictions**: See visual predictions of habit completion likelihood
- **Adaptive Suggestions**: Get tailored recommendations to align actions with objectives
- **Privacy-First Approach**: All processing happens locally with optional Gemini API integration

### 🧙‍♂️ "Onboarding Wizard"

Start your journey with a thoughtful setup process:

- **Guided Goal Setting**: Define what matters most to you
- **Habit Formation**: Create habits that align with your goals
- **Reward Definition**: Set up meaningful rewards that motivate you
- **Eco-Friendly Options**: Discover green habits with bonus incentives

### 🌍 Eco-Friendly Focus

Make sustainability a core part of your productivity:

- **Green Habits**: Track eco-friendly activities with bonus points
- **Environmental Impact**: Visualize your positive impact on the planet
- **Calm Earth Theme**: Enjoy a calming green-themed UI (primary color: #4CAF50)

### 📊 Insights Dashboard

Visualize your progress with an intuitive dashboard:

- **Progress Visualization**: Interactive charts for points, habits, and streaks
- **Activity Timeline**: Chronological record of completed habits
- **Goal Tracking**: Visual representation of progress toward goals
- **Motivational Feedback**: Encouraging messages based on your performance


## 🛠️ Technical Stack

Kore is built with modern, cross-platform technologies:

- **Kotlin Multiplatform**: Write once, run anywhere
- **Compose Multiplatform**: Beautiful, reactive UI
- **SQLDelight**: Type-safe SQL for database operations
- **Ktor**: Efficient networking and API communication
- **Kotlinx Coroutines**: Smooth asynchronous programming
- **Kotlinx Serialization**: Fast JSON processing
- **Material 3**: Modern design system with beautiful components

## 💻 Installation

### Prerequisites

- JDK 17 or higher
- Gradle 8.0 or higher

## 🚀 Quick Start

### Clone the Repository

```bash
git clone https://github.com/itza2k/Kore.git
cd kore
```

### Build the Project

```bash
.\gradlew build
```

### Run the Application

```bash
.\gradlew run
```

## 📁 Project Structure

```
kore/
├── composeApp/                  # Main application module
│   ├── src/
│   │   ├── commonMain/          # Cross-platform shared code
│   │   │   ├── kotlin/          # Kotlin source files
│   │   │   │   └── com/itza2k/kore/
│   │   │   │       ├── data/    # Data models
│   │   │   │       ├── db/      # Database operations
│   │   │   │       ├── ui/      # UI components
│   │   │   │       ├── util/    # Utilities
│   │   │   │       └── viewmodel/ # ViewModels
│   │   │   ├── composeResources/ # Compose resources
│   │   │   └── sqldelight/      # Database schema
│   │   └── desktopMain/         # Desktop-specific code
│   │       ├── kotlin/          # Desktop implementation
│   │       └── resources/       # Desktop resources
│   └── build.gradle.kts         # App build configuration
├── gradle/                      # Gradle configuration
└── build.gradle.kts             # Main build configuration
```

