package lv.mikeliskaneps.encyclopediaofcountries

import kotlinx.coroutines.Dispatchers
import lv.mikeliskaneps.encyclopediaofcountries.common.data.DataStoreRepository
import lv.mikeliskaneps.encyclopediaofcountries.networking.NetworkFactory
import lv.mikeliskaneps.encyclopediaofcountries.repository.ApiRepository
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

class EncyclopediaApplication : android.app.Application() {

    private val appModule = module {
        single(named("IODispatcher")) {
            Dispatchers.IO
        }
        single { DataStoreRepository(get()) }
        single {
            NetworkFactory(get()).countryApi()
        }
        single { ApiRepository(get(named("IODispatcher")), get()) }
        viewModel { CountryViewModel(get(), get()) }

    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@EncyclopediaApplication)
            modules(appModule)
        }
    }
}