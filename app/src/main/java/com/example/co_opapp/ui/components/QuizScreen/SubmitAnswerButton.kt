import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.co_opapp.SessionManager

@Composable
fun SubmitAnswerButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SessionManager.SUBMIT_BUTTON_PRIMARY_COLOR,
            contentColor = SessionManager.SUBMIT_BUTTON_TEXT_COLOR
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Text("Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
