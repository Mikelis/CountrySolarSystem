package lv.mikeliskaneps.encyclopediaofcountries.networking.api

import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponse
import retrofit2.Response
import retrofit2.http.GET

interface CountryApi {
    @GET("all")
    suspend fun getAllCountries(): Response<CountriesResponse>

}