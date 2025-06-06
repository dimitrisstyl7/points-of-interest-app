package dimstyl.pointsofinterest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val icon: ImageVector,
    val route: NavRoute,
    val navLabel: String = route.toString()
)

val navItems = listOf(
    NavItem(icon = Icons.Default.Place, route = NavRoute.DISCOVERIES),
    NavItem(icon = Icons.Default.Favorite, route = NavRoute.FAVORITES)
)