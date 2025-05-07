package dimstyl.pointsofinterest.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dimstyl.pointsofinterest.ui.components.ConfirmButton
import dimstyl.pointsofinterest.ui.components.DismissButton
import dimstyl.pointsofinterest.ui.theme.DialogContainerColor
import dimstyl.pointsofinterest.ui.theme.DialogIconContentColor
import dimstyl.pointsofinterest.ui.theme.DialogTitleContentColor

@Composable
fun RequestPermissionRationaleDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    openAppSettings: () -> Unit
) {
    AlertDialog(
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) },
        title = { Text(text = "Permission Required") },
        text = { Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined)) },
        containerColor = DialogContainerColor,
        titleContentColor = DialogTitleContentColor,
        textContentColor = Color.Unspecified,
        iconContentColor = DialogIconContentColor,
        onDismissRequest = onDismiss,
        confirmButton = if (isPermanentlyDeclined) {
            { ConfirmButton(text = "Grant Permission", onClick = openAppSettings) }
        } else {
            { DismissButton(text = "Ok", onClick = onDismiss) }
        },
        dismissButton = if (isPermanentlyDeclined) {
            { DismissButton(text = "Back", onClick = onDismiss) }
        } else null
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Camera permission was permanently denied. " +
                    "Please enable it in app settings to take a photo of your discovery."
        } else {
            "We need access to your camera so you can take a photo of your place discovery."
        }
    }
}

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Location access has been permanently denied. Please enable location " +
                    "in app settings to save your Point of Interest."
        } else {
            "This app needs your location to save your Point of Interest accurately."
        }
    }
}