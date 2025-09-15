

import android.util.Log
import com.example.co_opapp.data_model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface coopGameApi {

}


class CoopGameService : QuizService {

    // --- Backend API ---
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.21:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val gameApi: GameApiService = retrofit.create(GameApiService::class.java)

    // --- QuizService state flows ---
    private val _currentQuestion = MutableStateFlow<TriviaQuestion?>(null)
    override val currentQuestion: StateFlow<TriviaQuestion?> = _currentQuestion.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int> = _score.asStateFlow()

    private val _questionIndex = MutableStateFlow(0)
    override val questionIndex: StateFlow<Int> = _questionIndex.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0)
    override val totalQuestions: StateFlow<Int> = _totalQuestions.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // --- Multiplayer state ---
    private val _currentRoom = MutableStateFlow<GameRoom?>(null)
    val currentRoomFlow: StateFlow<GameRoom?> = _currentRoom.asStateFlow()

    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayerFlow: StateFlow<Player?> = _currentPlayer.asStateFlow()

    var isHost = false
        private set

    // --- QuizService implementations ---
    override suspend fun fetchNextQuestion() {
        try {
            val question = getRandomQuestion() // call your existing backend fetch
            if (question != null) {
                _currentQuestion.value = question
                _questionIndex.value += 1
                _totalQuestions.value += 1
            } else {
                _error.value = "Failed to fetch question"
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error"
        }
    }

    override suspend fun submitAnswer(answer: String): Boolean {
        val question = _currentQuestion.value ?: return false
        return try {
            val correct = submitAnswerToBackend(question.id, answer)
            if (correct) _score.value += 1
            _currentQuestion.value = null
            correct
        } catch (e: Exception) {
            _error.value = e.message
            false
        }
    }

    override fun resetGame() {
        _currentQuestion.value = null
        _score.value = 0
        _questionIndex.value = 0
        _totalQuestions.value = 0
        _error.value = null
    }

    // --- Multiplayer functions ---
    fun hostGame(player: Player, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = true
            _currentPlayer.value = player.copy(isHost = true)
            _currentRoom.value = GameRoom(
                hostId = player.id,
                players = listOf(_currentPlayer.value!!),
                currentPlayerIndex = 0,
                isGameStarted = false,
                gameState = GameState.WAITING
            )
            onSuccess()
        } catch (e: Exception) {
            Log.e("CoopGameService", "Error hosting game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun joinGame(player: Player, hostIp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            isHost = false
            _currentPlayer.value = player
            _currentRoom.value = GameRoom(
                hostId = "host_id",
                players = listOf(Player("host_id", "Host", isHost = true), player),
                currentPlayerIndex = 0,
                isGameStarted = false,
                gameState = GameState.WAITING
            )
            onSuccess()
        } catch (e: Exception) {
            Log.e("CoopGameService", "Error joining game", e)
            onError(e.message ?: "Unknown error")
        }
    }

    fun setPlayerReady(isReady: Boolean) {
        _currentPlayer.value?.let {
            val updated = it.copy(isReady = isReady)
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

    // --- Backend helpers ---
    private suspend fun getRandomQuestion(difficulty: String? = "easy", category: String? = null): TriviaQuestion? {
        return try {
            gameApi.getRandomQuestion(difficulty, category).body()
        } catch (e: Exception) {
            Log.e("CoopGameService", "Failed to fetch question", e)
            null
        }
    }

    private suspend fun submitAnswerToBackend(questionId: Long, answer: String): Boolean {
        return try {
            val response = gameApi.checkAnswer(AnswerRequest(questionId, answer))
            response.body()?.correct ?: false
        } catch (e: Exception) {
            Log.e("CoopGameService", "Failed to submit answer", e)
            false
        }
    }
}
