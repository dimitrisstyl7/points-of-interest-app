package dimstyl.pointsofinterest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dimstyl.pointsofinterest.ui.screens.main.MainScreen
import dimstyl.pointsofinterest.ui.theme.PointsOfInterestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointsOfInterestTheme {
                Surface(Modifier.fillMaxSize()) {
                    MainScreen(
                        openAppSettings = this::openAppSettings,
                        isPermanentlyDeclined = { permission ->
                            shouldShowRequestPermissionRationale(permission).not()
                        },
                        exitApp = { finish() })
                }
            }
        }
    }
}

private fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(this::startActivity)
}