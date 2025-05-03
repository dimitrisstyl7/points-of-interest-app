package dimstyl.pointsofinterest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavController,
    backHandler: @Composable () -> Unit,
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Places
    ) {
        composable<Places> {
        }

        composable<Favourites> {
        }
    }

    backHandler()
}