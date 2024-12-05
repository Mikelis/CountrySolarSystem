package lv.mikeliskaneps.encyclopediaofcountries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lv.mikeliskaneps.encyclopediaofcountries.navigation.event.EventParser
import lv.mikeliskaneps.encyclopediaofcountries.screens.DetailsScreen
import lv.mikeliskaneps.encyclopediaofcountries.screens.HomeScreen
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    countryViewModel: CountryViewModel = koinViewModel(),

    ) {
    EventParser(
        countryViewModel,
        navController = navController
    )

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home.route,
    ) {
        composable(Home.route) {
            HomeScreen(
                modifier, countryViewModel
            )
        }
        composable(Details.route) {
            DetailsScreen(
                modifier, countryViewModel
            )
        }
    }
}