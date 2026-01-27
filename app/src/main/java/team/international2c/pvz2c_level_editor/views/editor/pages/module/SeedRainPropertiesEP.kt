package team.international2c.pvz2c_level_editor.views.editor.pages.module

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.international2c.pvz2c_level_editor.data.PvzLevelFile
import team.international2c.pvz2c_level_editor.data.RtidParser
import team.international2c.pvz2c_level_editor.data.SeedRainItem
import team.international2c.pvz2c_level_editor.data.SeedRainPropertiesData
import team.international2c.pvz2c_level_editor.data.repository.PlantRepository
import team.international2c.pvz2c_level_editor.data.repository.ZombieRepository
import team.international2c.pvz2c_level_editor.views.components.AssetImage
import team.international2c.pvz2c_level_editor.views.editor.pages.others.EditorHelpDialog
import team.international2c.pvz2c_level_editor.views.editor.pages.others.HelpSection
import team.international2c.pvz2c_level_editor.views.editor.pages.others.NumberInputInt
import rememberJsonSync

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedRainPropertiesEP(
    rtid: String,
    rootLevelFile: PvzLevelFile,
    onBack: () -> Unit,
    onRequestPlantSelection: ((List<String>) -> Unit) -> Unit,
    onRequestZombieSelection: ((List<String>) -> Unit) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAddTypeDialog by remember { mutableStateOf(false) }

    // 编辑弹窗状态
    var editingItem by remember { mutableStateOf<SeedRainItem?>(null) }
    // 删除确认弹窗状态
    var deletingItem by remember { mutableStateOf<SeedRainItem?>(null) }

    val currentAlias = RtidParser.parse(rtid)?.alias ?: ""
    val obj = rootLevelFile.objects.find { it.aliases?.contains(currentAlias) == true }
    val syncManager = rememberJsonSync(obj, SeedRainPropertiesData::class.java)
    val dataState = syncManager.dataState

    fun sync() {
        syncManager.sync()
    }

    val themeColor = Color(0xFF009688)

    // 添加物品逻辑
    fun addItems(type: Int, ids: List<String>?) {
        val newList = dataState.value.seedRains.toMutableList()
        if (type == 2) {
            newList.add(SeedRainItem(seedRainType = 2))
        } else if (ids != null) {
            ids.forEach { id ->
                val newItem = when (type) {
                    0 -> SeedRainItem(seedRainType = 0, plantTypeName = id, zombieTypeName = null)
                    1 -> SeedRainItem(seedRainType = 1, zombieTypeName = id, plantTypeName = null)
                    else -> null
                }
                newItem?.let { newList.add(it) }
            }
        }
        dataState.value = dataState.value.copy(seedRains = newList)
        sync()
    }

    // 更新物品逻辑
    fun updateItem(oldItem: SeedRainItem, newItem: SeedRainItem) {
        val newList = dataState.value.seedRains.toMutableList()
        val index = newList.indexOf(oldItem)
        if (index != -1) {
            newList[index] = newItem
            dataState.value = dataState.value.copy(seedRains = newList)
            sync()
        }
    }

    // 删除物品逻辑
    fun deleteItem(item: SeedRainItem) {
        val newList = dataState.value.seedRains.toMutableList()
        newList.remove(item)
        dataState.value = dataState.value.copy(seedRains = newList)
        sync()
    }

    // === 弹窗 A: 添加类型选择 ===
    if (showAddTypeDialog) {
        AlertDialog(
            onDismissRequest = { showAddTypeDialog = false },
            title = { Text("添加罐子内容") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("植物 (Plant)") },
                        modifier = Modifier.clickable {
                            showAddTypeDialog = false
                            onRequestPlantSelection { ids -> addItems(0, ids) }
                        }
                    )
                    ListItem(
                        headlineContent = { Text("僵尸 (Zombie)") },
                        modifier = Modifier.clickable {
                            showAddTypeDialog = false
                            onRequestZombieSelection { ids ->
                                val processed = ids.map { ZombieRepository.buildAliases(it) }
                                addItems(1, processed)
                            }
                        }
                    )
                    ListItem(
                        headlineContent = { Text("道具 (Collectable)") },
                        modifier = Modifier.clickable {
                            showAddTypeDialog = false
                            addItems(2, null)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddTypeDialog = false
                }) { Text("取消") }
            },
            confirmButton = {}
        )
    }

    // === 弹窗 B: 编辑物品属性 ===
    if (editingItem != null) {
        val item = editingItem!!
        // 临时状态用于弹窗内修改
        var tempWeight by remember(item) { mutableStateOf(item.weight) }
        var tempMaxCount by remember(item) { mutableStateOf(item.maxCount) }

        val typeName = getSeedRainItemName(item)

        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { Text("编辑属性: $typeName") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    NumberInputInt(
                        value = tempWeight,
                        onValueChange = { tempWeight = it },
                        label = "权重 (Weight)",
                        modifier = Modifier.fillMaxWidth(),
                        color = themeColor
                    )
                    NumberInputInt(
                        value = tempMaxCount,
                        onValueChange = { tempMaxCount = it },
                        label = "数量上限 (MaxCount)",
                        modifier = Modifier.fillMaxWidth(),
                        color = themeColor
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateItem(item, item.copy(weight = tempWeight, maxCount = tempMaxCount))
                        editingItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) { Text("取消") }
            }
        )
    }

    // === 弹窗 C: 删除确认 ===
    if (deletingItem != null) {
        val item = deletingItem!!
        val typeName = getSeedRainItemName(item)
        AlertDialog(
            onDismissRequest = { deletingItem = null },
            title = { Text("确认删除") },
            text = { Text("确定要移除 $typeName 吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteItem(item)
                        deletingItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { deletingItem = null }) { Text("取消") }
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        topBar = {
            TopAppBar(
                title = { Text("种子雨设置", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, "说明", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (showHelpDialog) {
            EditorHelpDialog(
                title = "种子雨模块说明",
                onDismiss = { showHelpDialog = false },
                themeColor = themeColor
            ) {
                HelpSection(
                    title = "简要介绍",
                    body = "种子雨模块会让物品卡片按照一定时间间隔从天空中掉落。"
                )
                HelpSection(
                    title = "参数设置",
                    body = "权重决定掉落概率，上限决定最多存在的数量。注意大部分僵尸没有适配的僵尸卡片图标。"
                )
                HelpSection(
                    title = "植物阶级",
                    body = "该事件中掉落的植物卡片随玩家账号阶级，可以用全局阶级定义覆盖统一。"
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // === 顶部设置区域 ===
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    NumberInputInt(
                        value = dataState.value.rainInterval,
                        onValueChange = {
                            dataState.value = dataState.value.copy(rainInterval = it)
                            sync()
                        },
                        label = "掉落间隔 (秒)",
                        modifier = Modifier.fillMaxWidth(),
                        color = themeColor
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showAddTypeDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("添加掉落物品")
                    }
                }
            }

            // === 列表区域 ===
            if (dataState.value.seedRains.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无掉落配置，请添加", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(dataState.value.seedRains) { item ->
                        SeedRainRowCard(
                            item = item,
                            onClick = { editingItem = item },
                            onDelete = { deletingItem = item }
                        )
                    }
                }
            }
        }
    }
}

// 辅助函数：获取物品显示名称
@Composable
fun getSeedRainItemName(item: SeedRainItem): String {
    return when (item.seedRainType) {
        0 -> {
            val alias =
                RtidParser.parse(item.plantTypeName ?: "")?.alias ?: item.plantTypeName ?: ""
            PlantRepository.getName(alias)
        }

        1 -> {
            val alias =
                RtidParser.parse(item.zombieTypeName ?: "")?.alias ?: item.zombieTypeName ?: ""
            ZombieRepository.getName(alias)
        }

        2 -> "能量豆"
        else -> "未知"
    }
}

@Composable
fun SeedRainRowCard(
    item: SeedRainItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val typeName: String
    val displayType: String
    val iconPath: String?

    when (item.seedRainType) {
        0 -> {
            val alias =
                RtidParser.parse(item.plantTypeName ?: "")?.alias ?: item.plantTypeName ?: ""
            typeName = PlantRepository.getName(alias)
            val info = remember(alias) { PlantRepository.getPlantInfoById(alias) }
            iconPath = if (info?.icon != null) "images/plants/${info.icon}" else null
            displayType = "植物"
        }

        1 -> {
            val alias =
                RtidParser.parse(item.zombieTypeName ?: "")?.alias ?: item.zombieTypeName ?: ""
            typeName = ZombieRepository.getName(alias)
            val info = remember(alias) {
                ZombieRepository.search(
                    alias,
                    team.international2c.pvz2c_level_editor.data.repository.ZombieTag.All
                ).firstOrNull()
            }
            iconPath = if (info?.icon != null) "images/zombies/${info.icon}" else null
            displayType = "僵尸"
        }

        2 -> {
            typeName = "能量豆"
            iconPath = "images/others/plantfood.webp"
            displayType = "道具"
        }

        else -> {
            typeName = "未知"
            iconPath = null
            displayType = "未知"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (iconPath != null) {
                    AssetImage(
                        path = iconPath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        filterQuality = FilterQuality.Medium,
                        placeholder = {
                            Text(
                                typeName.take(1),
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // 概览信息
            Column(modifier = Modifier.weight(1f)) {
                Text(typeName, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(displayType, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text("权重: ${item.weight}", fontSize = 12.sp, color = Color(0xFF009688))
                    Spacer(Modifier.width(12.dp))
                    Text("上限: ${item.maxCount}", fontSize = 12.sp, color = Color(0xFF009688))
                }
            }

            // 删除按钮
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    tint = Color.LightGray
                )
            }
        }
    }
}