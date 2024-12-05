package lv.mikeliskaneps.encyclopediaofcountries.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lv.mikeliskaneps.encyclopediaofcountries.R
import lv.mikeliskaneps.encyclopediaofcountries.common.data.DataStoreRepository
import lv.mikeliskaneps.encyclopediaofcountries.navigation.Details
import lv.mikeliskaneps.encyclopediaofcountries.navigation.NavigationEvent
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponseItem
import lv.mikeliskaneps.encyclopediaofcountries.repository.ApiRepository
import timber.log.Timber

class CountryViewModel(
    private val repository: ApiRepository,
    private val dataStore: DataStoreRepository
) : ParentViewModel(dataStore) {

    private val _countriesLiveData = MutableLiveData<List<CountriesResponseItem>?>()

    private val _filteredCountriesLiveData = MutableLiveData<List<CountriesResponseItem>?>()
    val filteredCountriesLiveData: LiveData<List<CountriesResponseItem>?> get() = _filteredCountriesLiveData

    private val _selectedCountryLiveData = MutableLiveData<CountriesResponseItem>()
    val selectedCountryLiveData: LiveData<CountriesResponseItem> get() = _selectedCountryLiveData


    var favoritesLiveData: LiveData<List<CountriesResponseItem>?> =
        MutableLiveData<List<CountriesResponseItem>?>()
    private var queryJob: Job? = null

    init {
        getAllCountries()
    }


    fun getAllCountries() {
        viewModelScope.launch(exceptionHandler) {
            isLoading.value = true
            val response =
                repository.getAllCountries()
            _countriesLiveData.value = response
            getFavorites()
            isLoading.value = false
        }
    }

    private fun getFavorites() {
        favoritesLiveData = dataStore.stringListFlow.map { saved: List<String> ->
            _countriesLiveData.value?.filter { saved.contains(it.name) }
        }.asLiveData()
    }

    fun filterCountries(query: String, delayTime: Long = 400) {
        queryJob?.cancel()
        if (query.isBlank()) {
            _filteredCountriesLiveData.value = emptyList()
            return
        }
        queryJob = viewModelScope.launch(exceptionHandler) {
            delay(delayTime)
            isLoading.value = true
            _filteredCountriesLiveData.value = _countriesLiveData.value?.filter {
                var match = false
                match = it.name.lowercase().contains(query)
                if (!match) {
                    match = matchQueryList(it, it.translations?.values, query)
                }
                match
            }

            isLoading.value = false
        }
    }

    fun toggleFavorite(country: CountriesResponseItem) {
        viewModelScope.launch(exceptionHandler) {
            dataStore.toggleFavorites(country.name)
        }
    }

    fun isFavorite(country: CountriesResponseItem): Boolean {
        return favoritesLiveData.value?.contains(country) == true
    }

    fun goToDetails(country: CountriesResponseItem) {
        isLoading.value = true
        viewModelScope.launch(exceptionHandler) {
            _selectedCountryLiveData.value = country
            _navigationEvent.emit(NavigationEvent.Navigate(Details))
            isLoading.value = false
        }
    }

    fun goToDetailsByCountryCode(alpha3Code: String) {
        isLoading.value = true
        viewModelScope.launch(exceptionHandler) {
            _selectedCountryLiveData.value =
                _countriesLiveData.value?.first { it.alpha3Code == alpha3Code }
            _navigationEvent.emit(NavigationEvent.Navigate(Details))
            isLoading.value = false
        }
    }

    fun goToDetailsByLanguageCode(name: String) {
        isLoading.value = true
        viewModelScope.launch(exceptionHandler) {
            _selectedCountryLiveData.value = CountriesResponseItem(
                name = name,
                borders = getBordersByLanguageCode(name))
            isLoading.value = false
        }
    }

    fun getBordersByLanguageCode(name: String) : List<String> {
        val languageBorders = _countriesLiveData.value?.filter {
            it.languages?.map { it.iso6391 }?.contains(name) == true
        }
        return languageBorders?.map { it.alpha3Code ?: "" } ?: emptyList()
    }

    fun getCountriesInfoList(context: Context, country: CountriesResponseItem): List<String> {
        val list = mutableListOf<String?>()
        list.add(context.getString(R.string.name) + country.name)
        country.alpha3Code?.let {
            list.add(context.getString(R.string.code) + country.alpha3Code)
        }

        country.population?.let {
            list.add(context.getString(R.string.population) + country.population.toString())
            list.add(context.getString(R.string.population_rank) + getPopulationRank(it))
        }
        country.area?.let {
            list.add(context.getString(R.string.area) + country.area.toString())
        }



        return list.filterNotNull()
    }

    fun getPopulationRank(population: Int): Int {
        var maxRank = 1
        val countriesWithLargerPopulation =
            _countriesLiveData.value?.filter { (it.population ?: 0) > population }
        return maxRank + (countriesWithLargerPopulation?.size ?: 0)
    }

    fun matchQueryList(
        fullItem: CountriesResponseItem,
        item: Collection<String>?,
        query: String
    ): Boolean {
        var match = false
        item?.forEach {
            if (match) return@forEach
            match = it.lowercase().contains(query.lowercase())
            if (match) {
                Timber.e(it)
                fullItem.queryMatch = it
                return@forEach
            }
        }
        return match
    }
}

