package lv.mikeliskaneps.encyclopediaofcountries.networking.response

import com.google.gson.annotations.SerializedName

class CountriesResponse : ArrayList<CountriesResponseItem>()

data class CountriesResponseItem(
    val area: Double? = null,
    val borders: List<String>?,
    val alpha3Code: String? = null,
    val flag: String? = null,
    val flags: Flags? = null,
    val independent: Boolean? = null,
    val landlocked: Boolean? = null,
    val languages: List<Language>? = null,
    val name: String,
    val population: Int? = null,
    val region: String? = null,
    val startOfWeek: String? = null,
    val status: String? = null,
    val subregion: String? = null,
    val translations: Map<String,String>? = null,
    val unMember: Boolean? = null,
    var queryMatch: String? = null
)

data class Flags(
    val alt: String,
    val png: String,
    val svg: String
)

data class Language(
    @SerializedName("iso639_1")
    val iso6391: String,
    @SerializedName("iso639_2")
    val iso6392: String,
    val name: String,
    val nativeName: String
)
