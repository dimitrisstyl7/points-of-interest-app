package dimstyl.pointsofinterest.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import dimstyl.pointsofinterest.App
import dimstyl.pointsofinterest.ui.components.BottomBar
import dimstyl.pointsofinterest.ui.components.FloatingActionButton
import dimstyl.pointsofinterest.ui.components.TopBar
import dimstyl.pointsofinterest.ui.components.dialogs.CameraPermissionTextProvider
import dimstyl.pointsofinterest.ui.components.dialogs.LocationPermissionTextProvider
import dimstyl.pointsofinterest.ui.components.dialogs.NewDiscoveryDialog
import dimstyl.pointsofinterest.ui.components.dialogs.RequestPermissionRationaleDialog
import dimstyl.pointsofinterest.ui.components.dialogs.ViewDiscoveryDialog
import dimstyl.pointsofinterest.ui.navigation.AppNavHost
import dimstyl.pointsofinterest.ui.navigation.NavRoute
import dimstyl.pointsofinterest.ui.navigation.navItems
import dimstyl.pointsofinterest.ui.theme.BackgroundColor
import dimstyl.pointsofinterest.ui.utils.viewModelFactory
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
/*
Suppressed to access the current backstack size from NavController.
This is necessary to handle system back button behavior properly.

Note: Accessing the backstack size is a restricted API, so use this
carefully and ensure future updates to the navigation library do not
expose a safer alternative for this logic.
*/
@Composable
fun MainScreen(
    navController: NavController = rememberNavController(),
    viewModel: MainViewModel = viewModel<MainViewModel>(factory = viewModelFactory {
        MainViewModel(App.appModule.pointOfInterestRepository)
    }),
    openAppSettings: () -> Unit,
    showToast: (String, Int) -> Unit,
    isPermanentlyDeclined: (String) -> Boolean,
    createTempPhotoUri: () -> Uri,
    copyTempPhotoToPermanent: (Uri) -> Uri,
    exitApp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val backHandler = @Composable {
        BackHandler {
            val backStackSize = navController.currentBackStack.value.size

            when {
                backStackSize < 3 && state.currentNavItem.route == NavRoute.DISCOVERIES -> {
                    /*
                    If the back stack contains only two entries (the discoveries screen and the start
                    destination of the navigation graph), and the current route is the discoveries
                    screen ➡️ then exit the app
                    */
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

    val onSnackbarShow: (String, Boolean) -> Unit = { message, shortDuration ->
        scope.launch {
            val duration =
                if (shortDuration) SnackbarDuration.Short else SnackbarDuration.Long
            snackbarHostState.showSnackbar(message = message, duration = duration)
        }
    }

    Scaffold(
        topBar = { TopBar(state) },
        bottomBar = {
            BottomBar(
                mainState = state,
                onNavigate = { navItem ->
                    if (navItem.route != state.currentNavItem.route) {
                        navController.navigate(navItem.route.getRoute())
                        viewModel.setCurrentNavItem(navItem)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = BackgroundColor)
        ) {
            AppNavHost(
                navController = navController,
                backHandler = backHandler,
                viewModel = viewModel
            )
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 20.dp),
                onClick = { viewModel.showNewDiscoveryDialog(true) }
            )
        }
    }

    // NewDiscoveryDialog
    if (state.showNewDiscoveryDialog) {
        NewDiscoveryDialog(
            viewModel = viewModel,
            state = state,
            showSnackbar = { message, shortDuration ->
                onSnackbarShow(message, shortDuration)
            },
            showToast = showToast,
            createTempPhotoUri = createTempPhotoUri,
            copyTempPhotoToPermanent = copyTempPhotoToPermanent
        )
    }

    // ViewDiscoveryDialog
    if (state.showViewDiscoveryDialog) {
        ViewDiscoveryDialog(
            viewModel = viewModel,
            state = state,
            showSnackbar = { message, shortDuration ->
                onSnackbarShow(message, shortDuration)
            },
            showToast = showToast,
            createTempPhotoUri = createTempPhotoUri,
            copyTempPhotoToPermanent = copyTempPhotoToPermanent
        )
    }

    // Show RequestPermissionRationaleDialog if necessary
    state.permissions.reversed().forEach { permission ->
        RequestPermissionRationaleDialog(
            permissionTextProvider = when (permission) {
                Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> LocationPermissionTextProvider()

                else -> return@forEach
            },
            isPermanentlyDeclined = isPermanentlyDeclined(permission),
            onDismiss = { viewModel.dismissDialogPermission() },
            openAppSettings = openAppSettings
        )
    }
}