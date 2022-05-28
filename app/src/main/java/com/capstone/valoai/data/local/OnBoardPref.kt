package com.capstone.valoai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private const val ONBOARD_PREF = "onboard_pref"
val Context.datastore by preferencesDataStore(name = ONBOARD_PREF)

class OnBoardPref(private val datastore: DataStore<Preferences>) {
    private val isFirstLaunched = booleanPreferencesKey(("is_first_launched"))

    val launchStatus
        get() = datastore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[isFirstLaunched] ?: true
        }


    suspend fun updateIsFirstLaunchedToFalse() {
        datastore.edit { preferences ->
            preferences[isFirstLaunched] = false
        }
    }

}