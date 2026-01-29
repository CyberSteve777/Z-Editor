package team.international2c.pvz2c_level_editor.locale

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.dataStore by preferencesDataStore(name = "settings")

class LocaleManager(private val context: Context) {
    companion object {
        private const val SELECTED_LANGUAGE = "selected_language"
        private val LANGUAGE_KEY = stringPreferencesKey(SELECTED_LANGUAGE)
    }

    // Get saved locale from DataStore
    val currentLocale: Flow<Locale> = context.dataStore.data.map { preferences ->
        val languageTag = preferences[LANGUAGE_KEY] ?: Locale.getDefault().toLanguageTag()
        Locale.forLanguageTag(languageTag)
    }

    // Save locale to DataStore
    suspend fun setLocale(locale: Locale) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = locale.toLanguageTag()
        }
    }

    // Apply locale to configuration
    fun applyLocale(locale: Locale) {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    // Get supported locales
    fun getSupportedLocales(): List<Locale> = listOf(
        Locale.forLanguageTag("en"),
        Locale.forLanguageTag("zh"),
        Locale.forLanguageTag("ru")

    )
}