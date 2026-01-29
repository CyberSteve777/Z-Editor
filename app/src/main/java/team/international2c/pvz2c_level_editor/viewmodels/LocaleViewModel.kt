package team.international2c.pvz2c_level_editor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import team.international2c.pvz2c_level_editor.locale.LocaleManager
import java.util.Locale

class LocaleViewModel : ViewModel(), KoinComponent {
    private val localeManager: LocaleManager by inject()

    val currentLocale: StateFlow<Locale> = localeManager.currentLocale.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Locale.getDefault()
    )

    val supportedLocales: List<Locale> = localeManager.getSupportedLocales()

    fun changeLocale(locale: Locale) {
        viewModelScope.launch {
            localeManager.setLocale(locale)
            localeManager.applyLocale(locale)
        }
    }

    fun getDisplayName(locale: Locale): String {
        return when (locale.language) {
            "en" -> "English"
            "ru" -> "Русский"
            "zh" -> "语言"
            else -> locale.getDisplayLanguage(locale)
        }
    }
}