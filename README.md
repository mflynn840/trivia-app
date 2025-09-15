# Trivia Game App

A comprehensive Android trivia game with both single-player and co-op multiplayer modes, featuring a modern UI and backend integration.

## Features

- **Authentication**: Login/Register system (with testing bypass)
- **Single Player Mode**: Play trivia questions solo and test your knowledge
- **Co-op Multiplayer**: Host or join games on the same local network with up to 4 players
- **Trivia Questions**: Dynamic question loading from backend with fallback to sample questions
- **Modern UI**: Beautiful Jetpack Compose interface with Material Design 3
- **Backend Integration**: Spring Boot backend with question management and random question generation
- **Score Tracking**: Track your performance and see detailed results

## How to Play

### Single Player Mode
1. **Login**: Use the login screen or click "Skip Login (Testing)" for quick access
2. **Choose Mode**: Select "Single Player" from the game mode selection screen
3. **Play**: Answer trivia questions and see your score at the end

### Co-op Multiplayer Mode
1. **Login**: Use the login screen or click "Skip Login (Testing)" for quick access
2. **Choose Mode**: Select "Co-op Mode" from the game mode selection screen
3. **Lobby**: 
   - **Host a Game**: Click "Host Game" to create a new game room
   - **Join a Game**: Enter the host's IP address and click "Join Game"
4. **Ready Up**: All players must click "Ready" before the game can start
5. **Play**: 
   - The host clicks "Start Game" when everyone is ready
   - Players take turns answering trivia questions
   - See final scores and results

## Technical Details

- **Architecture**: MVVM with Jetpack Compose
- **Frontend**: Android app with Jetpack Compose UI
- **Backend**: Spring Boot REST API with JPA/Hibernate
- **Database**: SQLite for question storage
- **Networking**: Retrofit for API calls, Socket.IO for real-time communication (co-op mode)
- **Navigation**: Navigation Compose for screen transitions
- **State Management**: StateFlow for reactive UI updates
- **Question Management**: Dynamic loading with random question generation

## Setup

1. Build and install the app on multiple Android devices
2. Ensure all devices are on the same WiFi network
3. One device hosts the game, others join using the host's IP address
4. Start playing!

## Note

This is a simplified implementation for demonstration purposes. In a production app, you would need:
- A proper server for game state management
- More robust error handling
- Better security measures
- Persistent game sessions
