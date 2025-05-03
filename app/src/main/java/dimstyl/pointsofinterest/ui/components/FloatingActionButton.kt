package dimstyl.pointsofinterest.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dimstyl.pointsofinterest.ui.theme.AccentColor
import dimstyl.pointsofinterest.ui.theme.IconColor

@Composable
fun FloatingActionButton(modifier: Modifier, onClick: () -> Unit) {
    androidx.compose.material3.FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = AccentColor,
        contentColor = IconColor
    ) {
        Icon(Icons.Rounded.Add, "Add a new place")
    }
}