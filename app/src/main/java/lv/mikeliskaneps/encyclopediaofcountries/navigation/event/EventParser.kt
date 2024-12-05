package lv.mikeliskaneps.encyclopediaofcountries.navigation.event

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest
import lv.mikeliskaneps.encyclopediaofcountries.navigation.NavigationEvent
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.ParentViewModel

@Composable
fun EventParser(
    vararg viewModel: ParentViewModel,
    navController: NavHostController

) {
    val context = LocalContext.current

    viewModel.forEach { vm ->
        LaunchedEffect(vm.navigationEvent) {
            vm.navigationEvent.collectLatest {
                when (it) {
                    is NavigationEvent.Navigate -> {
                        val success =
                            navController.popBackStack(it.destination.route, false)
                        if (!success) {
                            navController.navigate(it.destination.route) {
                                popUpTo(it.destination.route) { inclusive = true }
                            }
                        }

                    }
                }
            }
        }

    }

    viewModel.forEach { vm ->
        LaunchedEffect(vm.errorEvent) {
            vm.errorEvent.collectLatest {
                Toast.makeText(
                    context,
                    ("Error " + it.message), Toast.LENGTH_LONG
                ).show()

            }
        }

    }

}
