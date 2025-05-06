package dimstyl.pointsofinterest.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dimstyl.pointsofinterest.ui.theme.DialogConfirmButtonTextColor

@Composable
fun ConfirmButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = DialogConfirmButtonTextColor)
    ) {
        Text(text = text)
    }
}