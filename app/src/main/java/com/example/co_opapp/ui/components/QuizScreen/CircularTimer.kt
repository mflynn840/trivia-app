import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.co_opapp.data_model.Timer
import kotlin.math.max

@Composable
fun CircularTimer(
    timer: Timer,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableStateOf(0L) }

    LaunchedEffect(timer) {
        while (true) {
            // Compute remaining time based on shared lobby state
            val now = System.currentTimeMillis()
            val remainingMs = (timer.startEpochTime + timer.durationMs - now)
            remainingSeconds = max(0L, remainingMs / 1000)
            delay(250L) // update 4 times per second for smoother countdown
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .background(Color.White, CircleShape)
            .border(2.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${remainingSeconds}s",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
