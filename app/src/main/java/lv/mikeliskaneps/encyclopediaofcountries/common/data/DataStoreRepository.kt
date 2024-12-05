package lv.mikeliskaneps.encyclopediaofcountries.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "country_data_preferences"
private const val FAVORITES = "favorites"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)


class DataStoreRepository(
    private val context: Context
) {
    var stringListFlow: Flow<List<String>> = getFavorites()


    fun getFavorites(): Flow<List<String>> {
        return context.dataStore.data
            .map { preferences ->
                val preferencesKey = stringPreferencesKey(FAVORITES)
                val csvString = preferences[preferencesKey] ?: ""
                if (csvString.isNotEmpty()) csvString.split(",") else emptyList()
            }
    }

    suspend fun toggleFavorites(item: String) {
        val preferencesKey = stringPreferencesKey(FAVORITES)
        val currentList = stringListFlow.first().toMutableList()
        val newList = if (currentList.contains(item) == true) {
            currentList.filter { it != item }
        } else {
            currentList.add(item)
            currentList
        }
        val jsonString = newList.joinToString(",")
        jsonString.let {
            context.dataStore.edit { preferences ->
                preferences[preferencesKey] = jsonString
            }
        }

    }


}