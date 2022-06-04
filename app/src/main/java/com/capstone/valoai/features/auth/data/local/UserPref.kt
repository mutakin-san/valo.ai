package com.capstone.valoai.features.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.valoai.features.auth.data.models.UserModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userDatastore by preferencesDataStore(name = "User Pref")

class UserPref(private val datastore: DataStore<Preferences>) {
    private val name = stringPreferencesKey(("name"))

    val user
        get() = datastore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[name]
        }


    suspend fun setUser(user: UserModel) {
        coroutineScope {
            datastore.edit { preferences ->
                preferences[name] = user.name ?: "Unknown"
            }

        }
    }


}