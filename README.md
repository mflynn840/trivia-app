# Trivia Co-op Game App

A full-stack Android multiplayer trivia game with a Spring Boot backend and SQLite database. Up to 4 players can join a game and answer trivia questions in turns.

## Features

### Backend (Spring Boot)
- **Authentication**: JWT-based login/register with encrypted passwords
- **Database**: SQLite with trivia questions loaded from JSON
- **REST API**: Complete API for authentication and game management
- **WebSocket Support**: Real-time game communication (ready for future expansion)
- **Question Management**: Load, fetch, and validate trivia questions

### Frontend (Android)
- **Authentication**: Secure login/register system
- **Trivia Gameplay**: Multiple choice questions with scoring
- **Turn-based System**: Players take turns answering questions
- **Real-time Updates**: Game state syncs across all players
- **Modern UI**: Jetpack Compose with Material Design 3

## Architecture

### Backend Stack
- **Spring Boot 3.5.5** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **SQLite** - Local database
- **JWT** - Token-based authentication
- **Jackson** - JSON processing

### Frontend Stack
- **Android** - Native Android app
- **Jetpack Compose** - Modern UI toolkit
- **Retrofit** - HTTP client for API calls
- **Navigation Compose** - Screen navigation
- **StateFlow** - Reactive state management

## Setup Instructions

### 1. Backend Setup

1. **Start the Backend Server**:
   ```bash
   # Windows
   start_backend.bat
   
   # Or manually
   cd backend
   mvn spring-boot:run
   ```

2. **Load Trivia Questions**:
   - Visit: `http://localhost:8080/api/game/questions/load`
   - This loads all questions from `questions.json` into the SQLite database

3. **Verify Backend**:
   - API Base URL: `http://localhost:8080`
   - Health Check: `http://localhost:8080/api/game/questions/random`

### 2. Frontend Setup

1. **Build and Install**:
   ```bash
   ./gradlew assembleDebug
   ```

2. **Configure Network**:
   - Ensure your Android device and backend are on the same network
   - Update the backend URL in `GameNetworkService.kt` if needed

3. **Run the App**:
   - Install the APK on your Android device(s)
   - Use "Skip Login (Testing)" for quick access

## How to Play

1. **Start Backend**: Run the backend server and load questions
2. **Login**: Register a new account or use existing credentials
3. **Lobby**: 
   - **Host**: Click "Host Game" to create a room
   - **Join**: Enter host's IP and click "Join Game"
4. **Ready Up**: All players click "Ready"
5. **Start Game**: Host clicks "Start Trivia Game"
6. **Play**: 
   - Players take turns answering questions
   - Correct answers earn points
   - Game continues for 10 rounds
   - Highest score wins!

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/validate` - Validate JWT token

### Game
- `GET /api/game/questions/random` - Get random question
- `POST /api/game/questions/check-answer` - Check answer
- `POST /api/game/questions/load` - Load questions from JSON

## Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique)
- `password` (Encrypted)
- `role` (USER/ADMIN)

### Questions Table
- `id` (Primary Key)
- `question` (Question text)
- `correct_answer` (Correct answer)
- `incorrect_answers` (JSON array of wrong answers)
- `category` (Question category)
- `difficulty` (easy/medium/hard)
- `type` (multiple/boolean)

## Development Notes

### Backend Configuration
- Database: SQLite (`trivia_game.db`)
- Port: 8080
- CORS: Enabled for all origins
- Security: JWT-based authentication

### Frontend Configuration
- Target SDK: 36
- Min SDK: 24
- Network Security: HTTP allowed for development

## Future Enhancements

- **WebSocket Integration**: Real-time multiplayer updates
- **Question Categories**: Filter by category/difficulty
- **Player Profiles**: Persistent user statistics
- **Game Modes**: Different game types and rules
- **Leaderboards**: Global and local high scores
- **Offline Mode**: Play without internet connection

## Troubleshooting

### Backend Issues
- Ensure port 8080 is available
- Check SQLite database file permissions
- Verify JSON file is in the correct location

### Frontend Issues
- Update backend URL in `GameNetworkService.kt`
- Check network connectivity
- Verify Android device and backend are on same network

## License

This project is for educational purposes. Feel free to use and modify as needed.
