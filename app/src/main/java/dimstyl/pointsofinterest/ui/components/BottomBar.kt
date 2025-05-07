package dimstyl.pointsofinterest.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dimstyl.pointsofinterest.ui.navigation.NavItem
import dimstyl.pointsofinterest.ui.navigation.navItems
import dimstyl.pointsofinterest.ui.screens.main.MainState
import dimstyl.pointsofinterest.ui.theme.AccentColor
import dimstyl.pointsofinterest.ui.theme.BottomBarColor
import dimstyl.pointsofinterest.ui.theme.NavBarIconColor
import dimstyl.pointsofinterest.ui.theme.UnselectedBottomBarItemColor

@Composable
fun BottomBar(
    mainState: MainState,
    onNavigate: (NavItem) -> Unit
) {
    NavigationBar(containerColor = BottomBarColor) {
        navItems.forEach {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = "Navigate to ${it.navLabel} screen"
                    )
                },
                label = { Text(it.navLabel) },
                selected = mainState.currentNavItem.route == it.route,
                onClick = { onNavigate(it) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavBarIconColor,
                    selectedTextColor = NavBarIconColor,
                    indicatorColor = AccentColor,
                    unselectedIconColor = UnselectedBottomBarItemColor,
                    unselectedTextColor = UnselectedBottomBarItemColor
                )
            )
        }
    }
}