package team.international2c.pvz2c_level_editor.views.screens.main

import android.content.Context
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.international2c.pvz2c_level_editor.Translator
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val themeColor = Color(0xFF4CAF50)
    val context = LocalContext.current
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Translator.t(context, "about_title"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Translator.t(context, "back"),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Language selector UI
            LanguageSelector()

            Text(
                text = Translator.t(context, "app_name"),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )

            Text(
                text = Translator.t(context, "subtitle"),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(18.dp))

            InfoSectionCard(title = Translator.t(context, "section_intro")) {
                Text(
                    text = Translator.t(context, "intro_text"),
                    lineHeight = 24.sp,
                    color = Color(0xFF424242)
                )
            }

            InfoSectionCard(title = Translator.t(context, "section_core_features")) {
                BulletPoint(Translator.t(context, "bullet_modular_editing"))
                BulletPoint(Translator.t(context, "bullet_multimode_support"))
                BulletPoint(Translator.t(context, "bullet_custom_injection"))
                BulletPoint(Translator.t(context, "bullet_auto_check"))
                BulletPoint(Translator.t(context, "bullet_preview"))
            }

            InfoSectionCard(title = Translator.t(context, "section_usage")) {
                Text(
                    text = Translator.t(context, "usage_text"),
                    lineHeight = 24.sp,
                    color = Color(0xFF424242)
                )
            }

            InfoSectionCard(title = Translator.t(context, "section_acknowledgements")) {
                BulletPoint(Translator.t(context, "author"))
                Text(
                    Translator.t(context, "author_name"),
                    lineHeight = 24.sp,
                    color = Color(0xFF424242)
                )
                BulletPoint(Translator.t(context, "special_thanks"))
                Text(
                    Translator.t(context, "thanks_names"),
                    lineHeight = 24.sp,
                    color = Color(0xFF424242)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = Translator.t(context, "tagline"),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = Translator.t(context, "version"),
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
fun LanguageSelector() {
    val context = LocalContext.current
    var selectedLang by remember { mutableStateOf("zh") }

    Row(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "中文",
            color = if (selectedLang == "zh") Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier
                .clickable {
                    selectedLang = "zh"
                    updateLocale(context, "zh")
                }
                .padding(end = 16.dp)
        )
        Text(
            text = "English",
            color = if (selectedLang == "en") Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier
                .clickable {
                    selectedLang = "en"
                    updateLocale(context, "en")
                }
        )
    }
}

// Function to change language at runtime
fun updateLocale(context: Context, lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

@Composable
fun InfoSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF388E3C)
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
        Text("• ", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Text(text, lineHeight = 24.sp, color = Color(0xFF424242))
    }
}
