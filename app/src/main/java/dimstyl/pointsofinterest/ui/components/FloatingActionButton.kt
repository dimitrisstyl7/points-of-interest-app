package dimstyl.pointsofinterest.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dimstyl.pointsofinterest.R
import dimstyl.pointsofinterest.ui.theme.AccentColor
import dimstyl.pointsofinterest.ui.theme.FloatingActionButtonColor

@Composable
fun FloatingActionButton(modifier: Modifier, onClick: () -> Unit) {
    androidx.compose.material3.FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = AccentColor,
        contentColor = FloatingActionButtonColor
    ) {
        Icon(
            painter = painterResource(R.drawable.add_place),
            contentDescription = "Add a new place"
        )
    }
}