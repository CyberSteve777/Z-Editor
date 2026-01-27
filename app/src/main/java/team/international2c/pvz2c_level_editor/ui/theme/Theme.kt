package team.international2c.pvz2c_level_editor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = Green300,
    onPrimary = Black,
    surface = DarkGray,
    onSurface = White,
    background = Black,
    onBackground = White
)

private val LightColorPalette = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    surface = White,
    onSurface = Black,
    background = LightGray,
    onBackground = Black
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
