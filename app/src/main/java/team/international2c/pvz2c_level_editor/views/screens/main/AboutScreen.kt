package team.international2c.pvz2c_level_editor.views.screens.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.* // optional, for more icons
import team.international2c.pvz2c_level_editor.MainActivity
import team.international2c.pvz2c_level_editor.R
import team.international2c.pvz2c_level_editor.Translator
import team.international2c.pvz2c_level_editor.viewmodels.ThemeViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.about_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = context.getString(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Settings(themeViewModel)
            Text(
                text = context.getString(R.string.app_name),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = context.getString(R.string.subtitle),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(18.dp))

            InfoSectionCard(title = context.getString(R.string.section_intro)) {
                Text(
                    text = context.getString(R.string.intro_text),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            InfoSectionCard(title = context.getString(R.string.section_core_features)) {
                BulletPoint(context.getString(R.string.bullet_modular_editing))
                BulletPoint(context.getString(R.string.bullet_multimode_support))
                BulletPoint(context.getString(R.string.bullet_custom_injection))
                BulletPoint(context.getString(R.string.bullet_auto_check))
                BulletPoint(context.getString(R.string.bullet_preview))
            }

            InfoSectionCard(title = context.getString(R.string.section_usage)) {
                Text(
                    text = context.getString(R.string.usage_text),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            InfoSectionCard(title = context.getString(R.string.section_acknowledgements)) {
                BulletPoint(context.getString(R.string.author))
                Text(
                    context.getString(R.string.author_name),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                BulletPoint(context.getString(R.string.special_thanks))
                Text(
                    context.getString(R.string.thanks_names),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = context.getString(R.string.tagline),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = context.getString(R.string.version),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun Settings(themeViewModel: ThemeViewModel) {
    Column {
        DarkModeSwitch(themeViewModel)
        LanguageSelector()
    }
}

@Composable
fun DarkModeSwitch(themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.dark_mode), modifier = Modifier.weight(1f))
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { themeViewModel.setTheme(it) }
        )
    }
}

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("en", "zh", "ru")
    var selectedLanguage by remember { mutableStateOf(Locale.getDefault().language) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.language), modifier = Modifier.weight(1f))
        Box {
            Text(
                text = selectedLanguage,
                modifier = Modifier.clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(text = language) },
                        onClick = {
                            selectedLanguage = language
                            updateLocale(context, language)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun updateLocale(context: Context, lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Restart the activity to apply language changes immediately
    if (context is Activity) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }
}

@Composable
fun InfoSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            content()
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
