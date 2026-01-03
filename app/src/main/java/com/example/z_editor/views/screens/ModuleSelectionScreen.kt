package com.example.z_editor.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.z_editor.data.datamodel.ModuleCategory
import com.example.z_editor.data.datamodel.ModuleMetadata
import com.example.z_editor.data.datamodel.ModuleRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSelectionScreen(
    existingObjClasses: Set<String>,
    onModuleSelected: (ModuleMetadata) -> Unit,
    onBack: () -> Unit
) {
    val allModules = remember { ModuleRegistry.getAllKnownModules() }

    // 状态管理
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ModuleCategory.Base) }

    // 核心过滤逻辑：分类 + 搜索
    val filteredModules = remember(selectedCategory, searchQuery) {
        allModules.entries
            .filter { (_, meta) ->
                // 1. 匹配分类
                val categoryMatch = meta.category == selectedCategory
                // 2. 匹配搜索 (标题、描述、别名)
                val searchMatch = if (searchQuery.isBlank()) true else {
                    meta.title.contains(searchQuery, ignoreCase = true) ||
                            meta.description.contains(searchQuery, ignoreCase = true) ||
                            meta.defaultAlias.contains(searchQuery, ignoreCase = true)
                }
                categoryMatch && searchMatch
            }
            .toList()
    }

    // 主题色 (保持编辑器风格的深绿色)
    val themeColor = Color(0xFF388E3C)

    Scaffold(
        topBar = {
            Surface(
                color = themeColor,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(bottom = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                        Spacer(Modifier.width(16.dp))

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text("搜索模块名称或描述", fontSize = 16.sp, color = Color.Gray)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = themeColor
                            ),
                            leadingIcon = {
                                Icon(Icons.Default.Search, null, tint = Color.Gray)
                            },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, null, tint = Color.Gray)
                                    }
                                }
                            } else null,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                    }

                    TabRow(
                        selectedTabIndex = ModuleCategory.entries.indexOf(selectedCategory),
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[ModuleCategory.entries.indexOf(selectedCategory)]),
                                color = Color.White, // 指示器为白色
                                height = 3.dp
                            )
                        },
                    ) {
                        ModuleCategory.entries.forEach { category ->
                            val isSelected = selectedCategory == category
                            Tab(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                text = {
                                    Text(
                                        text = category.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            if (filteredModules.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "未找到匹配 \"$searchQuery\" 的模块" else "该分类下暂无模块",
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredModules) { (objClass, meta) ->
                        val isAlreadyAdded = existingObjClasses.contains(objClass)

                        ModuleSelectionCard(
                            meta = meta,
                            isAlreadyAdded = isAlreadyAdded,
                            onClick = { if (!isAlreadyAdded) onModuleSelected(meta) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleSelectionCard(
    meta: ModuleMetadata,
    isAlreadyAdded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isAlreadyAdded) 0.6f else 1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isAlreadyAdded, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlreadyAdded) Color(0xFFEEEEEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isAlreadyAdded) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isAlreadyAdded) Color.Gray.copy(alpha = 0.1f)
                        else Color(0xFF388E3C).copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icon,
                    contentDescription = null,
                    tint = if (isAlreadyAdded) Color.Gray else Color(0xFF388E3C),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = meta.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isAlreadyAdded) Color.Gray else Color.Black
                    )
                }
                Text(
                    text = meta.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            if (isAlreadyAdded) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "已添加",
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}