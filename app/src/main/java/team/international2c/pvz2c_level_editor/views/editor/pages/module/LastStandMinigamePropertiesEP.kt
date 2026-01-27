package team.international2c.pvz2c_level_editor.views.editor.pages.module

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.international2c.pvz2c_level_editor.data.LastStandMinigamePropertiesData
import team.international2c.pvz2c_level_editor.data.PvzLevelFile
import team.international2c.pvz2c_level_editor.data.RtidParser
import team.international2c.pvz2c_level_editor.views.editor.pages.others.EditorHelpDialog
import team.international2c.pvz2c_level_editor.views.editor.pages.others.HelpSection
import team.international2c.pvz2c_level_editor.views.editor.pages.others.NumberInputInt
import rememberJsonSync

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastStandMinigamePropertiesEP(
    rtid: String,
    onBack: () -> Unit,
    rootLevelFile: PvzLevelFile,
    scrollState: ScrollState
) {
    val currentAlias = RtidParser.parse(rtid)?.alias ?: ""
    val focusManager = LocalFocusManager.current
    var showHelpDialog by remember { mutableStateOf(false) }

    val obj = rootLevelFile.objects.find { it.aliases?.contains(currentAlias) == true }
    val syncManager = rememberJsonSync(obj, LastStandMinigamePropertiesData::class.java)
    val moduleDataState = syncManager.dataState

    fun sync() {
        syncManager.sync()
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        topBar = {
            TopAppBar(
                title = { Text("坚不可摧配置", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, "Help", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (showHelpDialog) {
            EditorHelpDialog(
                title = "坚不可摧模块说明",
                onDismiss = { showHelpDialog = false },
                themeColor = Color(0xFF1976D2)
            ) {
                HelpSection(
                    title = "简要介绍",
                    body = "启用此模块后，关卡开始时会进入布阵阶段，不会立即出怪，允许玩家消耗初始阳光摆放植物。点击开始战斗后才会开始刷新波次。"
                )
                HelpSection(
                    title = "注意事项",
                    body = "在启用坚不可摧后，需要在波次管理器启用手动开始游戏开关，否则僵尸会自动出现，在添加或移除坚不可摧模块时软件会自动管理此开关。"
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("初始资源设置", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    NumberInputInt(
                        color = Color(0xFF1976D2),
                        value = moduleDataState.value.startingSun,
                        onValueChange = {
                            moduleDataState.value = moduleDataState.value.copy(startingSun = it)
                            sync()
                        },
                        label = "初始阳光 (StartingSun)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    NumberInputInt(
                        color = Color(0xFF1976D2),
                        value = moduleDataState.value.startingPlantfood,
                        onValueChange = {
                            moduleDataState.value =
                                moduleDataState.value.copy(startingPlantfood = it)
                            sync()
                        },
                        label = "初始能量豆 (StartingPlantfood)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EBF5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "添加坚不可摧模块后会自动在波次管理器模块里启用手动开始游戏开关。",
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}