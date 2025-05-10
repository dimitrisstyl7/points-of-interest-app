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
import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel
import dimstyl.pointsofinterest.ui.navigation.NavItem
import kotlinx.coroutines.Dispatchers
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

    fun setPointOfInterestUiModel(pointOfInterestUiModel: PointOfInterestUiModel) {
        _state.value = _state.value.copy(pointOfInterestUiModel = pointOfInterestUiModel)
    }

    fun setDraftPointOfInterestUiModel(pointOfInterestUiModel: PointOfInterestUiModel) {
        _state.value = _state.value.copy(draftPointOfInterestUiModel = pointOfInterestUiModel)
    }

    fun showNewDiscoveryDialog(show: Boolean) {
        _state.value = _state.value.copy(showNewDiscoveryDialog = show)
    }

    fun showViewDiscoveryDialog(show: Boolean) {
        _state.value = _state.value.copy(showViewDiscoveryDialog = show)
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

    fun setProcessingPointOfInterest(flag: Boolean) {
        _state.value = _state.value.copy(processingPointOfInterest = flag)
    }

    @SuppressLint("MissingPermission") // We know that location permission is already granted
    fun submitNewPointOfInterest(
        context: Context,
        photoUri: Uri?,
        copyTempPhotoToPermanent: (Uri) -> Uri,
        requestLocationPermission: () -> Unit,
        showToast: (String, Int) -> Unit,
        showSnackbar: (String, Boolean) -> Unit
    ) {
        setProcessingPointOfInterest(true)

        // Validate PointOfInterestUiModel and if has any errors, return.
        if (validatePointOfInterestUiModel()) {
            showToast("Please fill in all required fields", Toast.LENGTH_LONG)
            setProcessingPointOfInterest(false)
            return
        }

        // If location permission is not granted, request the permission and return.
        if (isLocationPermissionGranted(context).not()) {
            requestLocationPermission()
            setProcessingPointOfInterest(false)
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
                    setProcessingPointOfInterest(false)
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
                    setProcessingPointOfInterest(false)
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
                    setProcessingPointOfInterest(false)
                    showNewDiscoveryDialog(false)
                    setPointOfInterestUiModel(PointOfInterestUiModel())
                    runOnMainThread(context) {
                        showSnackbar(
                            "\"${pointOfInterest.title}\" has been added to your discoveries",
                            true
                        )
                    }
                })
            }
    }

    fun updatePointOfInterest(
        context: Context,
        photoUri: Uri?,
        copyTempPhotoToPermanent: (Uri) -> Uri,
        showToast: (String, Int) -> Unit,
        showSnackbar: (String, Boolean) -> Unit
    ) {
        setProcessingPointOfInterest(true)

        // Validate PointOfInterestUiModel and if has any errors, return.
        if (validatePointOfInterestUiModel()) {
            showToast("Please fill in all required fields", Toast.LENGTH_LONG)
            setProcessingPointOfInterest(false)
            return
        }

        val pointOfInterestUiModel = _state.value.pointOfInterestUiModel
        val draftPointOfInterestUiModel = _state.value.draftPointOfInterestUiModel

        // If pointOfInterest is equal with draftPointOfInterest, show success message and return.
        if (pointOfInterestUiModel.contentEquals(draftPointOfInterestUiModel)) {
            runOnMainThread(context) {
                showSnackbar(
                    "\"${pointOfInterestUiModel.title}\" has been updated successfully",
                    true
                )
            }
            setProcessingPointOfInterest(false)
            showViewDiscoveryDialog(false)
            setPointOfInterestUiModel(PointOfInterestUiModel())
            return
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
            setProcessingPointOfInterest(false)
            return
        }

        // Update point of interest
        val pointOfInterest =
            _state.value.draftPointOfInterestUiModel.copy(photoUri = uri?.toString()).toEntity()

        updatePointOfInterest(pointOfInterest = pointOfInterest, onSuccess = {
            setPointOfInterestUiModel(PointOfInterestUiModel())
            setDraftPointOfInterestUiModel(PointOfInterestUiModel())
            setProcessingPointOfInterest(false)
            showViewDiscoveryDialog(false)
            runOnMainThread(context) {
                showSnackbar(
                    "\"${pointOfInterestUiModel.title}\" has been updated successfully",
                    true
                )
            }
        })
    }

    fun deletePointOfInterest(context: Context, showSnackbar: (String, Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val pointOfInterest = state.value.pointOfInterestUiModel
            pointOfInterestRepository.delete(pointOfInterest.toEntity())
            setPointOfInterestUiModel(PointOfInterestUiModel())
            setDraftPointOfInterestUiModel(PointOfInterestUiModel())
            showViewDiscoveryDialog(false)
            runOnMainThread(context) {
                showSnackbar(
                    "Discovery “${pointOfInterest.title}” was deleted successfully",
                    true
                )
            }
        }
    }

    private fun savePointOfInterest(pointOfInterest: PointOfInterestEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            pointOfInterestRepository.save(pointOfInterest)
            onSuccess()
        }
    }

    private fun updatePointOfInterest(
        pointOfInterest: PointOfInterestEntity,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            pointOfInterestRepository.update(pointOfInterest)
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