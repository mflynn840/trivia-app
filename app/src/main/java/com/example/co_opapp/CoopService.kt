package com.example.co_opapp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.NetworkInterface

interface GameApiService {
    @GET("api/game/questions/random")
    suspend fun getRandomQuestion(
        @Query("difficulty") difficulty: String? = null,
        @Query("category") category: String? = null
    ): retrofit2.Response<TriviaQuestion>

    @POST("api/game/questions/check-answer")
    suspend fun checkAnswer(@Body answerRequest: AnswerRequest): retrofit2.Response<AnswerResponse>
}

class CoOpGameService {

    var gameApi: GameApiService? = null
    var isHost = false
        private set

    private val _gameState = MutableStateFlow<GameRoom?>(null)
    val gameState: StateFlow<GameRoom?> = _gameState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentRoom: GameRoom? = null
    private var currentPlayer: Player? = null

    init { initializeApi() }

    private fun initializeApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.4.21:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        gameApi = retrofit.create(GameApiService::class.java)
    }

    fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is java.net.Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error getting IP address", e)
            null
        }
    }

    fun startHosting(player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = true
            currentPlayer = player.copy(isHost = true)
            val localIp = getLocalIpAddress() ?: run { onError("Could not determine local IP"); return }
            currentRoom = GameRoom(hostId = player.id, players = listOf(currentPlayer!!))
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
            Log.d("CoOpGameService", "Hosting game on IP: $localIp")
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error hosting game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = false
            currentPlayer = player
            currentRoom = GameRoom(
                hostId = "host_id",
                players = listOf(Player(id = "host_id", username = "Host", isHost = true), player)
            )
            _gameState.value = currentRoom
            _connectionStatus.value = true
            onSuccess()
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Error joining game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun getMyPlayer(): Player? = currentPlayer

    fun setPlayerReady(isReady: Boolean) {
        currentPlayer?.let { player ->
            val updated = player.copy(isReady = isReady)
            currentPlayer = updated
            currentRoom?.let { room ->
                val updatedPlayers = room.players.map { if (it.id == updated.id) updated else it }
                currentRoom = room.copy(players = updatedPlayers)
                _gameState.value = currentRoom
            }
        }
    }

    fun isCurrentPlayerTurn(): Boolean {
        val room = currentRoom ?: return false
        val player = currentPlayer ?: return false
        return room.currentPlayerIndex < room.players.size &&
                room.players[room.currentPlayerIndex].id == player.id
    }

    fun getCurrentPlayer(): Player? {
        val room = currentRoom ?: return null
        return if (room.currentPlayerIndex < room.players.size) room.players[room.currentPlayerIndex] else null
    }

    fun completeTurn() {
        currentRoom?.let { room ->
            if (room.gameState == GameState.IN_PROGRESS) {
                val nextIndex = (room.currentPlayerIndex + 1) % room.players.size
                currentRoom = room.copy(currentPlayerIndex = nextIndex)
                _gameState.value = currentRoom
            }
        }
    }

    suspend fun getRandomQuestion(): TriviaQuestion? {
        return try { gameApi?.getRandomQuestion("easy", null)?.body() }
        catch (e: Exception) { Log.e("CoOpGameService", "Failed to get question", e); null }
    }

    suspend fun submitAnswer(questionId: Long, answer: String): Boolean {
        return try {
            val response = gameApi?.checkAnswer(AnswerRequest(questionId, answer))
            val correct = response?.body()?.correct ?: false
            if (correct) {
                currentPlayer?.let {
                    currentPlayer = it.copy(score = it.score + 1)
                    updateRoomPlayer(currentPlayer!!)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("CoOpGameService", "Failed to submit answer", e)
            false
        }
    }

    private fun updateRoomPlayer(updatedPlayer: Player) {
        currentRoom?.let { room ->
            val updatedPlayers = room.players.map { if (it.id == updatedPlayer.id) updatedPlayer else it }
            currentRoom = room.copy(players = updatedPlayers)
            _gameState.value = currentRoom
        }
    }

    fun startTriviaGame() {
        currentRoom?.let { room ->
            if (room.players.size >= 2) {
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
}
