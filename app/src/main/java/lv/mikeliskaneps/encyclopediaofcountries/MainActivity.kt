package lv.mikeliskaneps.encyclopediaofcountries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import lv.mikeliskaneps.encyclopediaofcountries.navigation.NavigationController
import lv.mikeliskaneps.encyclopediaofcountries.ui.theme.EncyclopediaOfCountriesTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EncyclopediaOfCountriesTheme {
                KoinContext {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Scaffold { innerPadding ->
                            NavigationController(
                                modifier = Modifier.padding(innerPadding),
                                navController = rememberNavController()
                            )
                        }
                    }
                }
            }
        }
    }
}
