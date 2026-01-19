package com.pedro.maschio.carcostsmanagement.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val INTRO_SHOWN = booleanPreferencesKey("intro_shown")
    val SELECTED_CAR_ID = longPreferencesKey("selected_car_id")
}

suspend fun setSelectedCarId(context: Context, carId: Long) {
    context.dataStore.edit {
        it[SettingsKeys.SELECTED_CAR_ID] = carId
    }
}

suspend fun setIntroShown(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.INTRO_SHOWN] = true
    }
}