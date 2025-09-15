package com.example.co_opapp.Service

import android.util.Log
import com.example.co_opapp.data_model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.Inet4Address
import java.net.NetworkInterface

// --- Retrofit API for backend question/answer checking ---
interface GameApiService {
    @GET("api/game/questions/random")
    suspend fun getRandomQuestion(
        @Query("difficulty") difficulty: String? = null,
        @Query("category") category: String? = null
    ): Response<TriviaQuestion>

    @POST("api/game/questions/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): Response<AnswerResponse>
}

// --- LAN + Game State Service ---
class CoopGameService {

    private var gameApi: GameApiService

    // --- Game state flows ---
    private val _currentRoom = MutableStateFlow<GameRoom?>(null)
    val currentRoomFlow: StateFlow<GameRoom?> = _currentRoom.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayerFlow: StateFlow<Player?> = _currentPlayer.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var isHost = false
        private set

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/") // LAN server IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        gameApi = retrofit.create(GameApiService::class.java)
    }

    // --- LAN Helpers ---
    fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error getting IP address", e)
            null
        }
    }

    fun hostGame(player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = true
            _currentPlayer.value = player.copy(isHost = true)
            val localIp = getLocalIpAddress() ?: run {
                onError("Unable to determine local IP")
                return
            }
            _currentRoom.value = GameRoom(
                hostId = player.id,
                players = listOf(_currentPlayer.value!!),
                currentPlayerIndex = 0,
                isGameStarted = false,
                gameState = GameState.WAITING
            )
            _connectionStatus.value = true
            Log.d("CoOpGameService", "Hosting game at $localIp")
            onSuccess()
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error hosting game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = false
            _currentPlayer.value = player
            // Simulate host + self
            _currentRoom.value = GameRoom(
                hostId = "host_id",
                players = listOf(Player("host_id", "Host", isHost = true), player),
                currentPlayerIndex = 0,
                isGameStarted = false,
                gameState = GameState.WAITING
            )
            _connectionStatus.value = true
            onSuccess()
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error joining game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    // --- Player utilities ---
    fun setPlayerReady(isReady: Boolean) {
        _currentPlayer.value?.let { player ->
            val updated = player.copy(isReady = isReady)
            _currentPlayer.value = updated
            updateRoomPlayer(updated)
        }
    }

    private fun updateRoomPlayer(updated: Player) {
        _currentRoom.value?.let { room ->
            val updatedPlayers = room.players.map { if (it.id == updated.id) updated else it }
            _currentRoom.value = room.copy(players = updatedPlayers)
        }
    }

    fun isCurrentPlayerTurn(): Boolean {
        val room = _currentRoom.value ?: return false
        val player = _currentPlayer.value ?: return false
        return room.currentPlayerIndex < room.players.size &&
                room.players[room.currentPlayerIndex].id == player.id
    }

    fun getCurrentPlayer(): Player? = _currentRoom.value?.players?.getOrNull(_currentRoom.value!!.currentPlayerIndex)

    fun completeTurn() {
        _currentRoom.value?.let { room ->
            if (room.gameState == GameState.IN_PROGRESS) {
                val nextIndex = (room.currentPlayerIndex + 1) % room.players.size
                _currentRoom.value = room.copy(currentPlayerIndex = nextIndex)
            }
        }
    }

    fun getMyPlayer(): Player? = _currentPlayer.value

    // --- Game flow ---
    fun startTriviaGame() {
        _currentRoom.value?.let { room ->
            if (room.players.size >= 2) {
                _currentRoom.value = room.copy(
                    isGameStarted = true,
                    gameState = GameState.IN_PROGRESS,
                    currentPlayerIndex = 0
                )
            }
        }
    }

    // --- Backend integration ---
    suspend fun getRandomQuestion(difficulty: String? = "easy", category: String? = null): TriviaQuestion? {
        return try { gameApi.getRandomQuestion(difficulty, category).body() }
        catch (e: Exception) { Log.e("CoOpGameService", "Failed to get question", e); null }
    }

    suspend fun submitAnswer(questionId: Long, answer: String): Boolean {
        return try {
            val response = gameApi.checkAnswer(AnswerRequest(questionId, answer))
            val correct = response.body()?.correct ?: false
            if (correct) incrementScore()
            correct
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Failed to submit answer", e)
            false
        }
    }

    private fun incrementScore() {
        _currentPlayer.value?.let {
            val updated = it.copy(score = it.score + 1)
            _currentPlayer.value = updated
            updateRoomPlayer(updated)
        }
    }
}
