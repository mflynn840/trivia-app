package com.example.co_opapp.ui.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.co_opapp.R
import com.example.co_opapp.Service.Backend.ProfileService
import com.example.co_opapp.Service.Coop.CurrentLobbyService
import com.example.co_opapp.SessionManager
import com.example.co_opapp.ui.components.GameOverScreen.ScoreCard
import com.example.co_opapp.ui.components.GameOverScreen.ScoreEntry
import com.example.co_opapp.ui.components.GameOverScreen.TrophyWithProfile
@Composable
fun GameOverScreen(
    currentLobbyService: CurrentLobbyService,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    profileService: ProfileService
) {
    val players by currentLobbyService.players
    val scores: Map<String, Int> by currentLobbyService.scores
    val username = SessionManager.currentPlayer?.username!!

    // Sort by score, highest first
    val sorted = players.sortedByDescending { scores[it.username] ?: 0 }

    // Build list of ScoreEntry
    val scoreEntries = sorted.mapIndexed { index, player ->
        ScoreEntry(
            place = index + 1,
            username = player.username,
            score = scores[player.username] ?: 0
        )
    }

    val topThree = sorted.take(3)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Scoreboard
        ScoreCard(scores = scoreEntries)

        Spacer(modifier = Modifier.height(32.dp))

        // Trophy row
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //2nd place trophy on the left
            if (topThree.size > 1) {
                TrophyWithProfile(
                    place = 2,
                    trophyRes = R.drawable.silver_trophy,
                    username = topThree[1].username,
                    profileService = profileService,
                )
            } else {
                Spacer(Modifier.size(100.dp))
            }

            //1st place trophy in the middle
            if (topThree.isNotEmpty()) {
                TrophyWithProfile(
                    place = 1,
                    trophyRes = R.drawable.gold_trophy,
                    username = topThree[0].username,
                    profileService = profileService,
                )
            } else {
                Spacer(Modifier.size(100.dp))
            }

            //3rd place trophy on the right
            if (topThree.size > 2) {
                TrophyWithProfile(
                    place = 3,
                    trophyRes = R.drawable.bronze_trophy,
                    username = topThree[2].username,
                    profileService = profileService,
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
