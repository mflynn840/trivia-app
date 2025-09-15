# Co-op LAN Game App

A simple Android multiplayer game where up to 4 players can join a LAN game and take turns clicking a button when their light is green.

## Features

- **Authentication**: Login/Register system (with testing bypass)
- **LAN Multiplayer**: Host or join games on the same local network
- **Turn-based Gameplay**: Players take turns clicking a button when their light is green
- **Real-time Updates**: Game state updates in real-time across all players
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## How to Play

1. **Login**: Use the login screen or click "Skip Login (Testing)" for quick access
2. **Lobby**: 
   - **Host a Game**: Click "Host Game" to create a new game room
   - **Join a Game**: Enter the host's IP address and click "Join Game"
3. **Ready Up**: All players must click "Ready" before the game can start
4. **Play**: 
   - The host clicks "Start Game" when everyone is ready
   - Players take turns - only the current player's light will be green
   - Click the button when it's your turn
   - The turn automatically passes to the next player

## Technical Details

- **Architecture**: MVVM with Jetpack Compose
- **Networking**: Socket.IO for real-time communication (simplified for demo)
- **Navigation**: Navigation Compose for screen transitions
- **State Management**: StateFlow for reactive UI updates

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
