package dimstyl.pointsofinterest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dimstyl.pointsofinterest.ui.screens.discoveries.DiscoveriesScreen
import dimstyl.pointsofinterest.ui.screens.favorites.FavoritesScreen
import dimstyl.pointsofinterest.ui.screens.main.MainViewModel

@Composable
fun AppNavHost(
    navController: NavController,
    backHandler: @Composable () -> Unit,
    viewModel: MainViewModel,
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Discoveries
    ) {
        composable<Discoveries> {
            DiscoveriesScreen(mainViewModel = viewModel)
        }

        composable<Favorites> {
            FavoritesScreen(mainViewModel = viewModel)
        }
    }

    backHandler()
}