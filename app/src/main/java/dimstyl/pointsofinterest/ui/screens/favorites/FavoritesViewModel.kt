package dimstyl.pointsofinterest.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dimstyl.pointsofinterest.data.PointOfInterestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val pointOfInterestRepository: PointOfInterestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            pointOfInterestRepository.getFavorites()
                .map { list -> list.map { it.toUiModel() } }
                .collect { uiModels -> _state.value = _state.value.copy(discoveries = uiModels) }
        }
    }

}