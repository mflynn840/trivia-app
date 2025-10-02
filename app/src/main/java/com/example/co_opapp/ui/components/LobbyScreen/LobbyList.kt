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
import com.example.co_opapp.ui.components.LoginScreen.Primary_NeonSignButton
import com.example.co_opapp.ui.components.LoginScreen.Secondary_NeonSignButton


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
            color = Color.Black,
        )

        // Action buttons (Join, Leave, Toggle Ready)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Primary_NeonSignButton(
                text = "Join",
                onClick = onJoin
            )
        }
    }
}

// Main composable that displays a scrollable list of lobby cards
@Composable
fun LobbyList(
    lobbyNames: List<String>,
    selectedLobbyName: String,
    onLobbySelect: (String) -> Unit,
    onJoinLobby: (String) -> Unit,
) {
    // LazyColumn displays a scrollable list of lobby cards
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(lobbyNames) { name ->
            LobbyCard(
                lobbyName = name,
                isSelected = selectedLobbyName == name,
                onSelect = { onLobbySelect(name) },
                onJoin = { onJoinLobby(name) },
            )
        }
    }
}

