import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.GameState
import com.example.co_opapp.data_model.Lobby

// Helper function to get the lobby status
fun getLobbyStatus(gameState: GameState): String {
    return when (gameState) {
        GameState.WAITING -> "Waiting"
        GameState.IN_PROGRESS -> "In Progress"
        GameState.FINISHED -> "Finished"
        GameState.WAITING_FOR_PLAYERS -> "Waiting for Players"
    }
}

// Composable function to display each lobby card
@Composable
fun LobbyCard(
    lobbyName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onJoin: () -> Unit,
) {

    // Define background color based on selection
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Column to arrange all elements within the card
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
            .clickable { onSelect() }
    ) {
        // Lobby name
        Text(
            text = "Lobby: $lobbyName",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        // Action buttons (Join, Leave, Toggle Ready)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onJoin) { Text("Join") }
        }
    }
}

// Main composable that displays a scrollable list of lobby cards
@Composable
fun LobbyList(
    lobbies: List<Lobby>,
    selectedLobbyName: String,
    onLobbySelect: (String) -> Unit,
    onJoinLobby: (Lobby) -> Unit,
) {
    // LazyColumn displays a scrollable list of lobby cards
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(lobbies) { lobby ->
            LobbyCard(
                lobbyName = lobby.name,
                isSelected = selectedLobbyName == lobby.name,
                onSelect = { onLobbySelect(lobby.name) },
                onJoin = { onJoinLobby(lobby) },
            )
        }
    }
}

