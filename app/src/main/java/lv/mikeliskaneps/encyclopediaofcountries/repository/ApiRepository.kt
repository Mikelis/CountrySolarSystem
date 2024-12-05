package lv.mikeliskaneps.encyclopediaofcountries.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lv.mikeliskaneps.encyclopediaofcountries.networking.api.CountryApi
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponse
import retrofit2.Response
import timber.log.Timber

class ApiRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val service: CountryApi
) {
    suspend fun getAllCountries(): CountriesResponse? {
        return withContext(ioDispatcher) {
            try {
                val response =
                    service.getAllCountries()
                return@withContext handleResponse(response)
            } catch (e: Exception) {
                Timber.e(e.toString())
                throw e
            }
        }
    }

    private fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()

        } else {
            handleErrorException(response)
        }
    }

    private fun <T> handleErrorException(response: Response<T>): Nothing {
        val error = response.errorBody()?.string()
        Timber.e(error)
        throw Throwable(
            message = error,
            cause = SimpleException(
                error
            )
        )
    }

    class SimpleException(msg: String?) : Throwable(msg)
}