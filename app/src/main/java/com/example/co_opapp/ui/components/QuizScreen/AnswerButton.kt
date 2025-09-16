package com.example.co_opapp.ui.components.QuizScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerButton(
    // The text to display on the button (answer option)
    text: String,
    // True if this answer is currently selected by the user
    isSelected: Boolean,
    // Callback invoked when the button is clicked
    onClick: () -> Unit,
    backgroundColor: Color
) {
    // Determine the button's background based on selection state
    val backgroundColor = if (isSelected) Color(0xFF4CAF50) // Green when selected
    else Color(0xCCB39DDB)  // Translucent purple when not selected
    // Determine text color based on selection
    val textColor = if (isSelected) Color.White else Color.Black

    // Surface provides a container with background color, shape, and click handling
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(vertical = 4.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(12.dp)) // black border
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {

        // Box used to center the text inside the button
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            // Display the answer text
            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = textColor // Text color changes if selected
            )
        }
    }
}
