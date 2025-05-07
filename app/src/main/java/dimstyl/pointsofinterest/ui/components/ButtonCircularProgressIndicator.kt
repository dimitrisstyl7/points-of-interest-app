package dimstyl.pointsofinterest.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dimstyl.pointsofinterest.ui.theme.ButtonCircularProgressIndicatorColor

@Composable
fun ButtonCircularProgressIndicator() {
    CircularProgressIndicator(
        color = ButtonCircularProgressIndicatorColor,
        trackColor = Color.Transparent
    )
}