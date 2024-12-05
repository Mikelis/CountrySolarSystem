package lv.mikeliskaneps.encyclopediaofcountries.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import lv.mikeliskaneps.encyclopediaofcountries.networking.api.CountryApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkFactory(
    private val context: Context,
    private val applyInterceptor: Boolean = true
) {
    companion object {
        const val HOST = "https://restcountries.com/v2/"
    }


    fun countryApi(): CountryApi {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            if (applyInterceptor) {
                this.addInterceptor(interceptor)
                    .cache(Cache(context.cacheDir, (5 * 1024 * 1024).toLong()))
                    // Add an Interceptor to the OkHttpClient.
                    .addInterceptor { chain ->
                        var request = chain.request()
                        request = if (hasNetwork(context) == true) {
                            request.newBuilder()
                                .header("Cache-Control", "public, max-age=" + 15 * 60)
                                .build()
                        } else {
                            request.newBuilder().header(
                                "Cache-Control",
                                "public, only-if-cached, max-stale=" + 60 * 60 * 24
                            ).build()
                        }

                        chain.proceed(request)
                    }
                    // time out setting
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
            }

        }
        val retrofit = Retrofit.Builder()
            .baseUrl(HOST)

            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: CountryApi = retrofit.create(CountryApi::class.java)
        return service
    }


    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}