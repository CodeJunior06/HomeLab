package com.uts.homelab.utils.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

     private val Context.dataStoreUserAuth by preferencesDataStore(name = "UserAuth")
     private val Context.dataStoreAppInfo by preferencesDataStore(name = "AppInfo")

    companion object {
        const val passAuth: String = "pass_auth"
        const val isNewInstall:String = "is_new_install"

        const val PREF_USER_AUTH = 0
        const val PREF_APP_INFO = 1
    }


    suspend fun setStringDataStore(namePref: Int, key: String, value: String) {
        try {

            val pref = when (namePref) {
                0 -> context.dataStoreUserAuth
                else -> context.dataStoreAppInfo
            }

            pref.edit { preferences ->

                val nameKey = stringPreferencesKey(key)

                preferences[nameKey] = value
            }
        } catch (e: IOException) {
            // Handle error
        }
    }

    suspend fun setBoolDataStore(namePref: Int, key: String, value: Boolean) {
        try {

            val pref = when (namePref) {
                0 -> context.dataStoreUserAuth
                else -> context.dataStoreAppInfo
            }

            pref.edit { preferences ->

                val nameKey = booleanPreferencesKey(key)

                preferences[nameKey] = value
            }
        } catch (e: IOException) {
            // Handle error
        }
    }


    fun getStringDataStore(namePref: Int, key: String): Flow<String?> {
        val pref = when (namePref) {
            0 -> context.dataStoreUserAuth
            else -> context.dataStoreAppInfo
        }

        val dataStoreKey = stringPreferencesKey(key)
        return pref.data.map { preferences ->
            preferences[dataStoreKey]
        }
    }


     fun getBoolDataStore(namePref: Int, key: String): Flow<Boolean?> {
        val pref = when (namePref) {
            0 -> context.dataStoreUserAuth
            else -> context.dataStoreAppInfo
        }

        val dataStoreKey = booleanPreferencesKey(key)

        return pref.data.map { preferences ->
            preferences[dataStoreKey]
        }
    }
}