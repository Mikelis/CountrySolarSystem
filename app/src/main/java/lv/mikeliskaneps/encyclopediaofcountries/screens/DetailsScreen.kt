package lv.mikeliskaneps.encyclopediaofcountries.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import lv.mikeliskaneps.encyclopediaofcountries.common.ui.ZoomableCircleGraphView
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: CountryViewModel
) {
    val filteredCountries by viewModel.selectedCountryLiveData.observeAsState()
    filteredCountries?.let {
        Box {
            ZoomableCircleGraphView(modifier, viewModel, country = it)
        }
    }

}