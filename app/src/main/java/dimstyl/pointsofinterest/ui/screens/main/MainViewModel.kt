package dimstyl.pointsofinterest.ui.screens.main

import androidx.lifecycle.ViewModel
import dimstyl.pointsofinterest.ui.navigation.NavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun setCurrentNavItem(navItem: NavItem) {
        val updatedNavItem = NavItem(
            icon = navItem.icon,
            route = navItem.route,
            navLabel = navItem.navLabel
        )
        _state.value = _state.value.copy(currentNavItem = updatedNavItem)
    }

}