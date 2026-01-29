package team.international2c.pvz2c_level_editor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import team.international2c.pvz2c_level_editor.locale.LocaleManager
import team.international2c.pvz2c_level_editor.viewmodels.LocaleViewModel
import team.international2c.pvz2c_level_editor.ui.theme.PVZ2LevelEditorTheme
import team.international2c.pvz2c_level_editor.viewmodels.ThemeViewModel
import team.international2c.pvz2c_level_editor.views.screens.main.AboutScreen
import team.international2c.pvz2c_level_editor.views.screens.main.EditorScreen
import team.international2c.pvz2c_level_editor.views.screens.main.LevelListScreen
import team.international2c.pvz2c_level_editor.views.screens.main.SettingsScreen

class MainActivity : ComponentActivity() {
    private val localeManager: LocaleManager by inject()
    private val themeViewModel: ThemeViewModel by viewModels()
    private val localeViewModel: LocaleViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(koinModule)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                localeViewModel.currentLocale.collect { locale ->
                    // Update the activity's context with the new locale
                    setContent {
                        val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
                        PVZ2LevelEditorTheme(darkTheme = isDarkTheme) {
                            AppNavigation(themeViewModel, localeViewModel)
                        }
                    }
                }
            }
        }
    }
}

enum class ScreenState {
    LevelList,
    Editor,
    About,
    Settings
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel,
                  localeViewModel: LocaleViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenState.LevelList) }
    var currentFileUri by remember { mutableStateOf<Uri?>(null) }
    var currentFileName by remember { mutableStateOf("") }

    AnimatedContent(
        targetState = currentScreen,
        label = "MainNavigationTransition",
        transitionSpec = {
            if (targetState == ScreenState.Editor || targetState == ScreenState.About || targetState == ScreenState.Settings) {
                (slideInHorizontally { width -> width } + fadeIn())
                    .togetherWith(
                        slideOutHorizontally { width -> -width / 3 } + fadeOut()
                    )
            } else {
                (slideInHorizontally { width -> -width } + fadeIn())
                    .togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
            }
        }
    ) { targetState ->
        when (targetState) {
            ScreenState.LevelList -> {
                LevelListScreen(
                    onLevelClick = { fileName, fileUri ->
                        currentFileName = fileName
                        currentFileUri = fileUri // 保存 Uri
                        currentScreen = ScreenState.Editor
                    },
                    onAboutClick = {
                        currentScreen = ScreenState.About
                    },
                    onSettingsClick = {
                        currentScreen = ScreenState.Settings
                    }
                )
            }

            ScreenState.Editor -> {
                EditorScreen(
                    fileUri = currentFileUri,
                    fileName = currentFileName,
                    onBack = {
                        currentScreen = ScreenState.LevelList
                    }
                )
            }

            ScreenState.About -> {
                AboutScreen(
                    onBack = {
                        currentScreen = ScreenState.LevelList
                    },
                    themeViewModel = themeViewModel
                )
            }

            ScreenState.Settings -> {
                SettingsScreen(
                    onBack = {
                        currentScreen = ScreenState.LevelList
                    },
                    themeViewModel = themeViewModel,
                    localeViewModel = localeViewModel
                )
            }
        }
    }
}
