package com.example.co_opapp

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.google.gson.annotations.SerializedName
import java.net.NetworkInterface

// API interfaces
interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body credentials: UserCredentials): retrofit2.Response<Map<String, String>>
    
    @POST("api/auth/login")
    suspend fun login(@Body credentials: UserCredentials): retrofit2.Response<LoginResponse>
    
    @POST("api/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): retrofit2.Response<Map<String, Any>>
}

interface GameApiService {
    @GET("api/game/questions/random")
    suspend fun getRandomQuestion(
        @Query("difficulty") difficulty: String? = null,
        @Query("category") category: String? = null
    ): retrofit2.Response<TriviaQuestion>
    
    @POST("api/game/questions/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): retrofit2.Response<AnswerResponse>
}

// Data classes
data class UserCredentials(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val username: String,
    val role: String
)

data class AnswerRequest(
    val questionId: Long,
    val answer: String
)

data class AnswerResponse(
    val correct: Boolean,
    val correctAnswer: String
)

class GameNetworkService {
    var authApi: AuthApiService? = null
    private var gameApi: GameApiService? = null
    private var authToken: String? = null
    
    var isHost = false
        private set
    private var currentPlayer: Player? = null
    private var currentRoom: GameRoom? = null
    
    private val _gameState = MutableStateFlow<GameRoom?>(null)
    val gameState: StateFlow<GameRoom?> = _gameState.asStateFlow()
    
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        initializeApiServices()
    }
    
    private fun initializeApiServices() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.24.160.1:8080/") // Backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        authApi = retrofit.create(AuthApiService::class.java)
        gameApi = retrofit.create(GameApiService::class.java)
    }
    
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error getting IP address", e)
        }
        return null
    }
    
    fun startHosting(player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = true
            currentPlayer = player.copy(isHost = true)
            
            // For LAN, we'll use a simple approach with a local server
            // In a real implementation, you'd need a local server running
            val localIp = getLocalIpAddress()
            if (localIp == null) {
                onError("Could not determine local IP address")
                return
            }
            
            // Create a room
            currentRoom = GameRoom(
                hostId = player.id,
                players = listOf(player.copy(isHost = true))
            )
            
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
            
            Log.d("GameNetworkService", "Hosting game on IP: $localIp")
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error hosting game", e)
            onError(e.message ?: "Unknown error")
        }
    }
    
    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = false
            currentPlayer = player
            
            // For simplicity, we'll simulate joining
            // In a real implementation, you'd connect to the host's server
            currentRoom = GameRoom(
                hostId = "host_id", // This would come from the server
                players = listOf(
                    Player(id = "host_id", username = "Host", isHost = true),
                    player
                )
            )
            
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
            
            Log.d("GameNetworkService", "Joined game on IP: $hostIp")
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Error joining game", e)
            onError(e.message ?: "Unknown error")
        }
    }
    
    fun setPlayerReady(isReady: Boolean) {
        currentPlayer?.let { player ->
            val updatedPlayer = player.copy(isReady = isReady)
            currentPlayer = updatedPlayer
            
            // Update room state
            currentRoom?.let { room ->
                val updatedPlayers = room.players.map { 
                    if (it.id == player.id) updatedPlayer else it
                }
                val updatedRoom = room.copy(players = updatedPlayers)
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun startGame() {
        if (!isHost) return
        
        currentRoom?.let { room ->
            if (room.players.size >= 2 && room.players.all { it.isReady }) {
                val updatedRoom = room.copy(
                    isGameStarted = true,
                    gameState = GameState.IN_PROGRESS,
                    currentPlayerIndex = 0
                )
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun completeTurn() {
        currentRoom?.let { room ->
            if (room.gameState == GameState.IN_PROGRESS) {
                val nextPlayerIndex = (room.currentPlayerIndex + 1) % room.players.size
                val updatedRoom = room.copy(currentPlayerIndex = nextPlayerIndex)
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
            }
        }
    }
    
    fun leaveGame() {
        currentPlayer = null
        currentRoom = null
        _gameState.value = null
        _connectionStatus.value = false
    }
    
    fun isCurrentPlayerTurn(): Boolean {
        val room = currentRoom ?: return false
        val player = currentPlayer ?: return false
        return room.currentPlayerIndex < room.players.size && 
               room.players[room.currentPlayerIndex].id == player.id
    }
    
    fun getCurrentPlayer(): Player? = currentRoom?.let { room ->
        if (room.currentPlayerIndex < room.players.size) {
            room.players[room.currentPlayerIndex]
        } else null
    }
    
    fun getCurrentPlayerInfo(): Player? = currentPlayer
    
    fun getMyPlayer(): Player? = currentPlayer
    
    // Authentication methods
    suspend fun register(username: String, password: String): Boolean {
        return try {
            val response = authApi?.register(UserCredentials(username, password))
            response?.isSuccessful == true
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Registration failed", e)
            false
        }
    }
    
    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = authApi?.login(UserCredentials(username, password))
            if (response?.isSuccessful == true) {
                val loginResponse = response.body()
                authToken = loginResponse?.token
                currentPlayer = Player(username = username)
                _connectionStatus.value = true
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Login failed", e)
            false
        }
    }
    
    // Trivia game methods
    suspend fun getRandomQuestion(): TriviaQuestion? {
        return try {
            val response = gameApi?.getRandomQuestion("easy", null)
            if (response?.isSuccessful == true) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Failed to get question", e)
            null
        }
    }
    
    suspend fun submitAnswer(questionId: Long, answer: String): Boolean {
        return try {
            val response = gameApi?.checkAnswer(AnswerRequest(questionId, answer))
            if (response?.isSuccessful == true) {
                val answerResponse = response.body()
                val isCorrect = answerResponse?.correct ?: false
                
                // Update player score if correct
                if (isCorrect) {
                    currentPlayer?.let { player ->
                        val updatedPlayer = player.copy(score = player.score + 1)
                        currentPlayer = updatedPlayer
                        updateGameState()
                    }
                }
                
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("GameNetworkService", "Failed to submit answer", e)
            false
        }
    }
    
    private fun updateGameState() {
        currentRoom?.let { room ->
            val updatedPlayers = room.players.map { player ->
                if (player.id == currentPlayer?.id) currentPlayer!! else player
            }
            val updatedRoom = room.copy(players = updatedPlayers)
            currentRoom = updatedRoom
            _gameState.value = updatedRoom
        }
    }
    
    fun startTriviaGame() {
        currentRoom?.let { room ->
            if (room.players.size >= 2) {
                val updatedRoom = room.copy(
                    isGameStarted = true,
                    gameState = GameState.IN_PROGRESS,
                    currentPlayerIndex = 0,
                    currentRound = 1
                )
                currentRoom = updatedRoom
                _gameState.value = updatedRoom
                
                // Load first question
                CoroutineScope(Dispatchers.IO).launch {
                    val question = getRandomQuestion()
                    question?.let { q ->
                        val roomWithQuestion = currentRoom?.copy(currentQuestion = q)
                        currentRoom = roomWithQuestion
                        _gameState.value = roomWithQuestion
                    }
                }
            }
        }
    }
    
    fun nextTurn() {
        currentRoom?.let { room ->
            val nextPlayerIndex = (room.currentPlayerIndex + 1) % room.players.size
            val updatedRoom = room.copy(currentPlayerIndex = nextPlayerIndex)
            currentRoom = updatedRoom
            _gameState.value = updatedRoom
            
            // Load next question
            CoroutineScope(Dispatchers.IO).launch {
                val question = getRandomQuestion()
                question?.let { q ->
                    val roomWithQuestion = currentRoom?.copy(currentQuestion = q)
                    currentRoom = roomWithQuestion
                    _gameState.value = roomWithQuestion
                }
            }
        }
    }
}
