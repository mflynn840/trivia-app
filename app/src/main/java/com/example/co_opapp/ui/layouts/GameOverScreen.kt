package com.example.co_opapp.ui.layouts

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.ui.components.GameOverScreen.TrophyWithProfile
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import kotlin.collections.getValue


@Composable
fun GameOverScreen(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {

    //Reactively consume exposed variables in lobbyService
    val players by currentLobbyService.players // Map<String, UserDTO>
    val scores: Map<String, Int> by currentLobbyService.scores   // Map<String, Int>

    // Sort by score, highest first
    val sorted = players.sortedByDescending { scores[it.username] ?: 0 }

    // Take top 3
    val topThree = sorted.take(3)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2nd place
            if (topThree.size > 1) {
                TrophyWithProfile(
                    place = 2,
                    //imageUri = topThree[1].profilePictureUri,
                    imageUri=null,
                    trophyRes = R.drawable.silver_trophy
                )
            } else {
                Spacer(Modifier.size(100.dp))
            }

            // 1st place
            if (topThree.isNotEmpty()) {
                TrophyWithProfile(
                    place = 1,
                    imageUri = null,
                    trophyRes = R.drawable.gold_trophy
                )
            } else {
                Spacer(Modifier.size(100.dp))
            }

            // 3rd place
            if (topThree.size > 2) {
                TrophyWithProfile(
                    place = 3,
                    imageUri = null,
                    trophyRes = R.drawable.bronze_trophy
                )
            } else {
                Spacer(Modifier.size(100.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateBack) {
            Text("Back to Lobby")
        }
    }
}
