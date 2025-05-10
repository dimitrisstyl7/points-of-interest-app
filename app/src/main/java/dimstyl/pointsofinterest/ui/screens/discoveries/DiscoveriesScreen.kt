package dimstyl.pointsofinterest.ui.screens.discoveries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dimstyl.pointsofinterest.App
import dimstyl.pointsofinterest.ui.components.Card
import dimstyl.pointsofinterest.ui.screens.main.MainViewModel
import dimstyl.pointsofinterest.ui.utils.viewModelFactory

@Composable
fun DiscoveriesScreen(
    viewModel: DiscoveriesViewModel = viewModel<DiscoveriesViewModel>(factory = viewModelFactory {
        DiscoveriesViewModel(App.appModule.pointOfInterestRepository)
    }),
    mainViewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val discoveries = state.discoveries
        items(items = discoveries, key = { it.id!! }) {
            Card(it) {
                mainViewModel.setPointOfInterestUiModel(it)
                mainViewModel.setDraftPointOfInterestUiModel(it)
                mainViewModel.showViewDiscoveryDialog(true)
            }
        }
    }
}