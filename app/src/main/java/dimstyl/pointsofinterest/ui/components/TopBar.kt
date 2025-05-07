package dimstyl.pointsofinterest.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dimstyl.pointsofinterest.ui.screens.main.MainState
import dimstyl.pointsofinterest.ui.theme.NavBarIconColor
import dimstyl.pointsofinterest.ui.theme.TopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(mainState: MainState) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = mainState.currentNavItem.icon,
                    tint = NavBarIconColor,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = mainState.currentNavItem.navLabel, color = NavBarIconColor)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TopBarColor
        )
    )
}