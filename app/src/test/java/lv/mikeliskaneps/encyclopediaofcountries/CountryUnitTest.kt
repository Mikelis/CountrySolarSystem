package lv.mikeliskaneps.encyclopediaofcountries

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import lv.mikeliskaneps.encyclopediaofcountries.networking.api.CountryApi
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponse
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponseItem
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.Language
import lv.mikeliskaneps.encyclopediaofcountries.repository.ApiRepository
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: CountryViewModel
    private lateinit var repository: ApiRepository

    @Before
    fun setUp() {
        val response = CountriesResponse()
        response.add(
            CountriesResponseItem(
                name = "Latvia",
                languages = listOf(
                    Language(
                        iso6391 = "LV",
                        name = "Latvian",
                        nativeName = "Latvian",
                        iso6392 = ""
                    )
                ),
                population = 10,
                borders = listOf("LT,EE")
            )
        )
        response.add(CountriesResponseItem(name = "Estonia", population = 5, borders = emptyList()))
        response.add(
            CountriesResponseItem(
                name = "Lithuania",
                population = 15,
                borders = emptyList()
            )
        )
        val mockApi = mockk<CountryApi> {
            coEvery { getAllCountries() } returns Response.success(
                response
            )
        }
        repository = ApiRepository(Dispatchers.IO, mockApi)
        viewModel = CountryViewModel(repository, mockk())

    }

    @Test
    fun checkIfPopulationRankIsCorrect() = runTest {
        viewModel.getAllCountries()
        assert(viewModel.getPopulationRank(10) == 2)
        assert(viewModel.getPopulationRank(5) == 3)
        assert(viewModel.getPopulationRank(15) == 1)

    }

    @Test
    fun checkQueryFinder() = runTest {
        assert(
            viewModel.matchQueryList(
                CountriesResponseItem(
                    name = "Latvia",
                    population = 10,
                    borders = emptyList()
                ), item = listOf("Latvia", "Estonia", "Lithuania"), query = "Lat"
            ) == true
        )

        assert(
            viewModel.matchQueryList(
                CountriesResponseItem(
                    name = "Estonia",
                    population = 10,
                    borders = emptyList()
                ), item = listOf("Latvia", "Estonia", "Lithuania"), query = "Lat"
            ) == true
        )

        assert(
            viewModel.matchQueryList(
                CountriesResponseItem(
                    name = "Estonia",
                    population = 10,
                    borders = emptyList()
                ), item = listOf("Latvia", "Estonia", "Lithuania"), query = "ton"
            ) == true
        )

        assert(
            viewModel.matchQueryList(
                CountriesResponseItem(
                    name = "Estonia",
                    population = 10,
                    borders = emptyList()
                ), item = listOf("Latvia", "Estonia", "Lithuania"), query = "USA"
            ) == false
        )
    }

    @Test
    fun checkIfCountriesFoundByLanguage() = runTest {
        viewModel.getAllCountries()
        assert(viewModel.getBordersByLanguageCode("LV").size == 1)

    }


}
