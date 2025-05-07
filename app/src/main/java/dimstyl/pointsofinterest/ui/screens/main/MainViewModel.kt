package dimstyl.pointsofinterest.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Priority
import dimstyl.pointsofinterest.App
import dimstyl.pointsofinterest.data.PointOfInterestEntity
import dimstyl.pointsofinterest.data.PointOfInterestRepository
import dimstyl.pointsofinterest.ui.navigation.NavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val pointOfInterestRepository: PointOfInterestRepository
) : ViewModel() {

    private val fusedLocationProviderClient = App.appModule.fusedLocationProviderClient
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun setCurrentNavItem(navItem: NavItem) {
        _state.value = _state.value.copy(currentNavItem = navItem)
    }

    fun setNewPointOfInterestUiModel(pointOfInterestUiModel: PointOfInterestUiModel) {
        _state.value = _state.value.copy(pointOfInterestUiModel = pointOfInterestUiModel)
    }

    fun showNewPlaceDialog(show: Boolean) {
        _state.value = _state.value.copy(showNewPlaceDialog = show)
    }

    fun dismissDialogPermission() {
        _state.value.permissions.firstOrNull()?.let {
            val updatedPermissions = _state.value.permissions
                .toMutableList().apply { removeAt(0) } // remove first permission (FIFO)
            _state.value = _state.value.copy(permissions = updatedPermissions)
        }
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted) {
            val updatedPermissions =
                _state.value.permissions.toMutableList().apply { add(permission) }
            _state.value = _state.value.copy(permissions = updatedPermissions)
        }
    }

    fun setSavingPointOfInterest(flag: Boolean) {
        _state.value = _state.value.copy(savingPointOfInterest = flag)
    }

    @SuppressLint("MissingPermission") // We know that location permission is already granted
    fun submitNewPointOfInterest(
        context: Context,
        photoUri: Uri?,
        copyTempPhotoToPermanent: (Uri) -> Uri,
        requestPermission: () -> Unit,
        showToast: (String, Int) -> Unit,
        showSnackbar: (String, Boolean) -> Unit
    ) {
        setSavingPointOfInterest(true)

        // Validate PointOfInterestUiModel and if has any errors, return.
        if (validatePointOfInterestUiModel()) {
            showToast("Please fill in all required fields", Toast.LENGTH_LONG)
            setSavingPointOfInterest(false)
            return
        }

        // If location permission is not granted, request the permission and return.
        if (isLocationPermissionGranted(context).not()) {
            requestPermission()
            setSavingPointOfInterest(false)
            return
        }

        fusedLocationProviderClient
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->
                // If location is null, inform user and return.
                if (location == null) {
                    runOnMainThread(context) {
                        showToast(
                            "Unable to retrieve your location. Please check if your " +
                                    "location is turned on and try again.",
                            Toast.LENGTH_LONG
                        )
                    }
                    setSavingPointOfInterest(false)
                    return@addOnSuccessListener
                }

                /*
                If no photo is captured, uri is null.
                Otherwise, it contains the actual uri of the saved photo,
                or Uri.EMPTY if something went wrong.
                */
                val uri = photoUri?.let {
                    // Copy photo from cache to permanent storage
                    copyTempPhotoToPermanent(it)
                }

                /*
                If uri is not null and uri == Uri.EMPTY, return.
                It means that something went wrong while saving the photo.
                */
                if (uri != null && uri == Uri.EMPTY) {
                    runOnMainThread(context) {
                        showToast(
                            "Failed to save your photo. Please try again.",
                            Toast.LENGTH_SHORT
                        )
                    }
                    setSavingPointOfInterest(false)
                    return@addOnSuccessListener
                }

                // Save point of interest to database
                val pointOfInterest =
                    _state.value.pointOfInterestUiModel.copy(
                        longitude = location.longitude,
                        latitude = location.latitude,
                        photoUri = uri?.toString()
                    ).toEntity()

                savePointOfInterest(pointOfInterest = pointOfInterest, onSuccess = {
                    runOnMainThread(context) {
                        showSnackbar(
                            "\"${pointOfInterest.title}\" has been added to your places",
                            true
                        )
                    }
                    setSavingPointOfInterest(false)
                    showNewPlaceDialog(false)
                    setNewPointOfInterestUiModel(PointOfInterestUiModel())
                })
            }
    }

    private fun savePointOfInterest(pointOfInterest: PointOfInterestEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            pointOfInterestRepository.addPointOfInterest(pointOfInterest)
            onSuccess()
        }
    }

    private fun validatePointOfInterestUiModel(): Boolean {
        // Title validation
        val titleError = _state.value.pointOfInterestUiModel.title.isBlank()

        // Category validation
        val categoryError = _state.value.pointOfInterestUiModel.category.isBlank()

        // Rating validation
        val rating = _state.value.pointOfInterestUiModel.rating
        val min = _state.value.pointOfInterestUiModel.minRating
        val max = _state.value.pointOfInterestUiModel.maxRating
        val ratingError = rating.isBlank() || rating.toInt() !in min..max

        return titleError || categoryError || ratingError
    }

    private fun isLocationPermissionGranted(context: Context): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission || coarseLocationPermission
    }

    private fun runOnMainThread(context: Context, runnable: () -> Unit) {
        ContextCompat.getMainExecutor(context).execute(runnable)
    }

}