package dimstyl.pointsofinterest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import dimstyl.pointsofinterest.ui.screens.main.MainScreen
import dimstyl.pointsofinterest.ui.theme.PointsOfInterestTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PointsOfInterestTheme {
                Surface(Modifier.fillMaxSize()) {
                    MainScreen(
                        openAppSettings = ::openAppSettings,
                        showToast = { message, duration -> showToast(message, duration) },
                        isPermanentlyDeclined = { permission ->
                            shouldShowRequestPermissionRationale(permission).not()
                        },
                        createTempPhotoUri = ::createTempPhotoUri,
                        copyTempPhotoToPermanent = ::copyTempPhotoToPermanent,
                        exitApp = ::finish
                    )
                }
            }
        }
    }
}

private fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

private fun Activity.showToast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

private fun Activity.createTempPhotoUri(): Uri {
    val filename = "${UUID.randomUUID()}.jpg"
    val imageFile = File(cacheDir, filename)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        imageFile
    )
}

private fun Activity.copyTempPhotoToPermanent(uri: Uri): Uri {
    val inputStream = try {
        contentResolver.openInputStream(uri)
            ?: throw RuntimeException("Input stream cannot be null")
    } catch (e: FileNotFoundException) {
        Log.e("ImageCopy", "File not found for URI: $uri", e)
        return Uri.EMPTY
    } catch (e: RuntimeException) {
        Log.e("ImageCopy", "Runtime exception while opening stream: $uri", e)
        return Uri.EMPTY
    }

    val fileName = uri.lastPathSegment ?: "${UUID.randomUUID()}.jpg"
    val destFile = File(filesDir, fileName)

    inputStream.use { input ->
        FileOutputStream(destFile).use { output ->
            input.copyTo(output)
        }
    }

    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        destFile
    )
}