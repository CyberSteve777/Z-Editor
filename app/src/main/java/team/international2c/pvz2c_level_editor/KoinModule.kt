package team.international2c.pvz2c_level_editor

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import team.international2c.pvz2c_level_editor.locale.LocaleManager
import team.international2c.pvz2c_level_editor.viewmodels.LocaleViewModel


val koinModule = module {
    single { LocaleManager(androidContext()) }
    viewModelOf(::LocaleViewModel)
}
