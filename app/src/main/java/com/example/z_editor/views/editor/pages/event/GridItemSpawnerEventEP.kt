package com.example.z_editor.views.editor.pages.event

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.z_editor.data.GridItemSpawnerZombieData
import com.example.z_editor.data.PvzLevelFile
import com.example.z_editor.data.RtidParser
import com.example.z_editor.data.SpawnZombiesFromGridItemData
import com.example.z_editor.data.repository.GridItemRepository
import com.example.z_editor.data.repository.ZombiePropertiesRepository
import com.example.z_editor.data.repository.ZombieRepository
import com.example.z_editor.data.repository.ZombieTag
import com.example.z_editor.views.components.AssetImage
import com.example.z_editor.views.editor.pages.others.EditorHelpDialog
import com.example.z_editor.views.editor.pages.others.HelpSection
import com.example.z_editor.views.editor.pages.others.NumberInputInt
import com.example.z_editor.views.editor.pages.others.StepperControl
import com.google.gson.Gson

private val gson = Gson()

// ======================== 编辑器界面 ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpawnZombiesFromGridItemSpawnerEventEP(
    rtid: String,
    onBack: () -> Unit,
    rootLevelFile: PvzLevelFile,
    onRequestGridItemSelection: ((String) -> Unit) -> Unit,
    onRequestZombieSelection: ((String) -> Unit) -> Unit,
    scrollState: LazyListState
) {
    val currentAlias = RtidParser.parse(rtid)?.alias ?: ""
    val focusManager = LocalFocusManager.current
    var showHelpDialog by remember { mutableStateOf(false) }

    val themeColor = Color(0xFF607D8B)

    val actionDataState = remember {
        val obj = rootLevelFile.objects.find { it.aliases?.contains(currentAlias) == true }
        val data = try {
            gson.fromJson(obj?.objData, SpawnZombiesFromGridItemData::class.java)
        } catch (_: Exception) {
            SpawnZombiesFromGridItemData()
        }
        mutableStateOf(data)
    }

    val internalObjectAliases = remember(rootLevelFile.objects.size, rootLevelFile.hashCode()) {
        rootLevelFile.objects.flatMap { it.aliases ?: emptyList() }.toSet()
    }

    fun sync() {
        rootLevelFile.objects.find { it.aliases?.contains(currentAlias) == true }?.let {
            it.objData = gson.toJsonTree(actionDataState.value)
        }
    }

    fun addGridType() {
        onRequestGridItemSelection { typeName ->
            val newList = actionDataState.value.gridTypes.toMutableList()
            newList.add(RtidParser.build(typeName, "GridItemTypes"))
            actionDataState.value = actionDataState.value.copy(gridTypes = newList)
            sync()
        }
    }

    fun addZombie() {
        onRequestZombieSelection { typeName ->
            val newList = actionDataState.value.zombies.toMutableList()
            newList.add(
                GridItemSpawnerZombieData(
                    type = RtidParser.build(typeName, "ZombieTypes"),
                    level = 1
                )
            )
            actionDataState.value = actionDataState.value.copy(zombies = newList)
            sync()
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("编辑 $currentAlias", fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("事件类型：障碍物出怪", fontSize = 14.sp, fontWeight = FontWeight.Normal)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, "帮助", tint = Color.White)
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
                title = "障碍物出怪事件说明",
                onDismiss = { showHelpDialog = false },
                themeColor = themeColor
            ) {
                HelpSection(
                    title = "简要介绍",
                    body = "此事件可以在特定的障碍物类型上进行出怪，常用于黑暗时代的亡灵返乡。"
                )
                HelpSection(
                    title = "生成事件",
                    body = "僵尸会从障碍物所在的格子生成。每个障碍物只能出现1只僵尸，若需要出现多只可以尝试重复引用事件。"
                )
                HelpSection(
                    title = "延迟时间",
                    body = "从波次开始到僵尸生成之间的时间间隔，如果计时尚未结束已经进入下一波则不会进行出怪。"
                )
            }
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("基础参数", color = themeColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = actionDataState.value.waveStartMessage ?: "",
                            onValueChange = {
                                val newVal = if (it.isBlank()) null else it
                                actionDataState.value = actionDataState.value.copy(waveStartMessage = newVal)
                                sync()
                            },
                            label = { Text("提示信息 (WaveStartMessage)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                focusedLabelColor = themeColor
                            )
                        )
                        Text(
                            "事件开始时在屏幕中央显示的红字警告，不支持输入中文",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        NumberInputInt(
                            value = actionDataState.value.zombieSpawnWaitTime,
                            onValueChange = {
                                actionDataState.value = actionDataState.value.copy(zombieSpawnWaitTime = it)
                                sync()
                            },
                            color = themeColor,
                            label = "生成延迟 (秒)",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "僵尸生成前的等待时间，若已进入下一波将不生成僵尸",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "触发源障碍物 (GridTypes)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = themeColor
                    )
                    Button(
                        onClick = { addGridType() },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("添加类型", fontSize = 13.sp)
                    }
                }
            }

            items(actionDataState.value.gridTypes.size) { index ->
                val rtidStr = actionDataState.value.gridTypes[index]

                val parsed = RtidParser.parse(rtidStr)
                val alias = parsed?.alias ?: rtidStr
                val source = parsed?.source

                val isValid = if (source == "CurrentLevel") {
                    internalObjectAliases.contains(alias)
                } else {
                    GridItemRepository.isValid(alias)
                }

                val displayName = if (source == "CurrentLevel") alias else GridItemRepository.getName(alias)

                Card(
                    colors = CardDefaults.cardColors(containerColor = if (!isValid) Color(0xFFF8F1F1) else Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    border = if (!isValid) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssetImage(
                            path = GridItemRepository.getIconPath(alias),
                            contentDescription = alias,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEEEEEE)),
                            filterQuality = FilterQuality.Medium,
                            placeholder = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Widgets, null, tint = Color.Gray)
                                }
                            }
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                            Text(
                                text = alias,
                                fontSize = 10.sp,
                                color = if (!isValid) Color.Red else Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                val newList = actionDataState.value.gridTypes.toMutableList()
                                newList.removeAt(index)
                                actionDataState.value = actionDataState.value.copy(gridTypes = newList)
                                sync()
                            }
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color.LightGray)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "生成僵尸 (Zombies)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = themeColor
                    )
                    Button(
                        onClick = { addZombie() },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("添加僵尸", fontSize = 13.sp)
                    }
                }
            }

            items(actionDataState.value.zombies.size) { index ->
                val zombieData = actionDataState.value.zombies[index]

                val parsed = RtidParser.parse(zombieData.type)
                val alias = parsed?.alias ?: zombieData.type
                val source = parsed?.source

                val isValid = if (source == "CurrentLevel") {
                    internalObjectAliases.contains(alias)
                } else {
                    ZombieRepository.isValid(alias)
                }

                val realTypeName = ZombiePropertiesRepository.getTypeNameByAlias(alias)
                val info = ZombieRepository.search(realTypeName, ZombieTag.All).firstOrNull()
                val displayName = info?.name ?: realTypeName

                Card(
                    colors = CardDefaults.cardColors(containerColor = if (!isValid) Color(0xFFF8F1F1) else Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    border = if (!isValid) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssetImage(
                                path = if (info?.icon != null) "images/zombies/${info.icon}" else null,
                                contentDescription = displayName,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEEEEEE)),
                                filterQuality = FilterQuality.Medium,
                                placeholder = {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(displayName.take(1), fontWeight = FontWeight.Bold)
                                    }
                                }
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(alias, fontSize = 10.sp, color = Color.Gray)
                            }


                            IconButton(
                                onClick = {
                                    val newList = actionDataState.value.zombies.toMutableList()
                                    val zombieToCopy = newList[index]
                                    newList.add(index + 1, zombieToCopy.copy())

                                    actionDataState.value = actionDataState.value.copy(zombies = newList)
                                    sync()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = {
                                    val newList = actionDataState.value.zombies.toMutableList()
                                    newList.removeAt(index)
                                    actionDataState.value = actionDataState.value.copy(zombies = newList)
                                    sync()
                                }
                            ) {
                                Icon(Icons.Default.Delete, null, tint = Color.LightGray)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        StepperControl(
                            label = "僵尸等级",
                            valueText = "${zombieData.level}",
                            onMinus = {
                                val current = zombieData.level
                                if (current > 1) {
                                    val newList = actionDataState.value.zombies.toMutableList()
                                    newList[index] = zombieData.copy(level = current - 1)
                                    actionDataState.value = actionDataState.value.copy(zombies = newList)
                                    sync()
                                }
                            },
                            onPlus = {
                                val current = zombieData.level
                                if (current < 10) {
                                    val newList = actionDataState.value.zombies.toMutableList()
                                    newList[index] = zombieData.copy(level = current + 1)
                                    actionDataState.value = actionDataState.value.copy(zombies = newList)
                                    sync()
                                }
                            },
                            modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(48.dp)) }
        }
    }
}