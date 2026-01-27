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
import team.international2c.pvz2c_level_editor.ui.theme.AppTheme
import team.international2c.pvz2c_level_editor.viewmodels.ThemeViewModel
import team.international2c.pvz2c_level_editor.views.screens.main.AboutScreen
import team.international2c.pvz2c_level_editor.views.screens.main.EditorScreen
import team.international2c.pvz2c_level_editor.views.screens.main.LevelListScreen

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            AppTheme(darkTheme = isDarkTheme) {
                AppNavigation(themeViewModel)
            }
        }
    }
}

enum class ScreenState {
    LevelList,
    Editor,
    About
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenState.LevelList) }
    var currentFileUri by remember { mutableStateOf<Uri?>(null) }
    var currentFileName by remember { mutableStateOf("") }

    AnimatedContent(
        targetState = currentScreen,
        label = "MainNavigationTransition",
        transitionSpec = {
            if (targetState == ScreenState.Editor || targetState == ScreenState.About) {
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
        }
    }
}