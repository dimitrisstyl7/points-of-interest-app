package dimstyl.pointsofinterest.ui.screens.main

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dimstyl.pointsofinterest.ui.components.BottomBar
import dimstyl.pointsofinterest.ui.components.FloatingActionButton
import dimstyl.pointsofinterest.ui.components.TopBar
import dimstyl.pointsofinterest.ui.navigation.AppNavHost
import dimstyl.pointsofinterest.ui.navigation.NavRoute
import dimstyl.pointsofinterest.ui.navigation.navItems
import dimstyl.pointsofinterest.ui.theme.BackgroundColor
import dimstyl.pointsofinterest.ui.utils.viewModelFactory
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
/*
* Suppressed to access the current backstack size from NavController.
* This is necessary to handle system back button behavior properly.
*
* Note: Accessing the backstack size is a restricted API, so use this
* carefully and ensure future updates to the navigation library do not
* expose a safer alternative for this logic.
* */
@Composable
fun MainScreen(
    navController: NavController = rememberNavController(),
    viewModel: MainViewModel = viewModel<MainViewModel>(factory = viewModelFactory { MainViewModel() }),
    exitApp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val mainState by viewModel.state.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()

    val backHandler = @Composable {
        BackHandler {
            val backStackSize = navController.currentBackStack.value.size

            when {
                backStackSize < 3 && mainState.currentNavItem.route == NavRoute.PLACES -> {
                    /*
                    * If the back stack contains only two entries (the places screen and the start
                    *   destination of the navigation graph), and the current route is the places
                    *   screen (PLACES) ➡️ then exit the app
                    * */
                    exitApp()
                }

                navController.navigateUp() -> {
                    scope.launch {
                        navItems.forEach {
                            if (entry?.destination?.hasRoute((it.route.getRoute()::class)) == true) {
                                viewModel.setCurrentNavItem(it)
                            }
                        }
                    }
                }

                else -> exitApp()
            }
        }
    }

    Scaffold(
        topBar = { TopBar(mainState) },
        bottomBar = {
            BottomBar(
                mainState = mainState,
                onNavigate = { navItem ->
                    if (navItem.route != mainState.currentNavItem.route) {
                        navController.navigate(navItem.route.getRoute())
                        viewModel.setCurrentNavItem(navItem)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = BackgroundColor)
        ) {
            AppNavHost(
                navController = navController,
                backHandler = backHandler
            )
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 20.dp)
            ) { }
        }
    }
}