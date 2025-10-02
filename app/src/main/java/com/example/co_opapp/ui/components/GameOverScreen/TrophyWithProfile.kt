package com.example.co_opapp.ui.components.GameOverScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.example.co_opapp.Service.Backend.ProfileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun TrophyWithProfile(
    place: Int,
    trophyRes: Int,
    username: String,
    profileService: ProfileService
) {

    // Load profile picture asynchronously
    var profileBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(username) {
        val bitmap = withContext(Dispatchers.IO) {
            profileService.getProfilePicture() // Returns Bitmap?
        }
        profileBitmap = bitmap
    }

    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp) // Trophy container
        ) {
            // Trophy image
            Image(
                painter = painterResource(id = trophyRes),
                contentDescription = "Trophy for place $place",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            // Profile overlay
            profileBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Player profile",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        // Username label below trophy
        Text(
            text = username,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}
