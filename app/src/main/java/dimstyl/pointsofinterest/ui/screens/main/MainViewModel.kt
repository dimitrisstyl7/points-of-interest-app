package dimstyl.pointsofinterest.ui.screens.main

import androidx.lifecycle.ViewModel
import dimstyl.pointsofinterest.ui.navigation.NavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

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

    fun validatePointOfInterestUiModel(): Boolean {
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

    fun savePointOfInterest() {
        // TODO
    }

}