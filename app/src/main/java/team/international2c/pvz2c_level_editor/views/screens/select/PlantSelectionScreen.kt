package team.international2c.pvz2c_level_editor.views.screens.select

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.international2c.pvz2c_level_editor.data.repository.PlantCategory
import team.international2c.pvz2c_level_editor.data.repository.PlantInfo
import team.international2c.pvz2c_level_editor.data.repository.PlantRepository
import team.international2c.pvz2c_level_editor.data.repository.PlantTag
import team.international2c.pvz2c_level_editor.views.components.AssetImage

@Composable
fun PlantSelectionScreen(
    isMultiSelect: Boolean = false,
    onMultiPlantSelected: (List<String>) -> Unit = {},
    onPlantSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var searchQuery by remember { mutableStateOf("") }
    // 默认进入“按品质”
    var selectedCategory by remember { mutableStateOf(PlantCategory.Quality) }
    var selectedTag by remember { mutableStateOf(PlantTag.All) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    val favoriteIds = PlantRepository.favoriteIds

    val currentVisibleTags = remember(selectedCategory) {
        if (selectedCategory == PlantCategory.Collection) {
            emptyList()
        } else {
            listOf(PlantTag.All) + PlantTag.entries.filter {
                it.category == selectedCategory && it != PlantTag.All
            }
        }
    }

    val displayList = remember(searchQuery, selectedTag, selectedCategory, favoriteIds.size) {
        PlantRepository.search(searchQuery, selectedTag, selectedCategory)
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != PlantCategory.Collection) {
            if (!currentVisibleTags.contains(selectedTag)) {
                selectedTag = currentVisibleTags.firstOrNull() ?: PlantTag.All
            }
        }
    }

    val themeColor = Color(0xFF8BC34A)

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
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
                                Text(
                                    if (isMultiSelect) "已选择 ${selectedIds.size} 项，点击搜索" else "搜索植物名称或代码",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
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
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                    }) { Icon(Icons.Default.Clear, null, tint = Color.Gray) }
                                }
                            } else null,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                    }
                    ScrollableTabRow(
                        selectedTabIndex = PlantCategory.entries.indexOf(selectedCategory),
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            val index = PlantCategory.entries.indexOf(selectedCategory)
                            if (index < tabPositions.size) {
                                SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[index]),
                                    color = Color.White,
                                    height = 3.dp
                                )
                            }
                        },
                        divider = {}
                    ) {
                        PlantCategory.entries.forEach { category ->
                            val isSelected = selectedCategory == category
                            Tab(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (category == PlantCategory.Collection) {
                                            Icon(
                                                Icons.Default.Star,
                                                null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if(isSelected) Color.White else Color.White.copy(0.6f)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = category.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 15.sp
                                        )
                                    }
                                },
                                unselectedContentColor = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    if (selectedCategory != PlantCategory.Collection) {
                        Spacer(Modifier.height(4.dp))
                        ScrollableTabRow(
                            selectedTabIndex = currentVisibleTags.indexOf(selectedTag).coerceAtLeast(0),
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                val index = currentVisibleTags.indexOf(selectedTag)
                                if (index != -1 && index < tabPositions.size) {
                                    Box(
                                        Modifier
                                            .tabIndicatorOffset(tabPositions[index])
                                            .height(2.5.dp)
                                            .padding(horizontal = 4.dp)
                                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(1.dp))
                                    )
                                }
                            },
                            divider = {},
                            modifier = Modifier.height(40.dp)
                        ) {
                            currentVisibleTags.forEach { tag ->
                                val isTagSelected = selectedTag == tag
                                Tab(
                                    selected = isTagSelected,
                                    onClick = { selectedTag = tag },
                                    modifier = Modifier.height(40.dp),
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (tag.iconName != null) {
                                                AssetImage(
                                                    path = "images/tags/${tag.iconName}",
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp),
                                                    placeholder = {}
                                                )
                                                Spacer(Modifier.width(6.dp))
                                            }
                                            Text(
                                                text = tag.label,
                                                fontWeight = if (isTagSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp,
                                                color = if (isTagSelected) Color.White else Color.White.copy(0.6f)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (isMultiSelect) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { onMultiPlantSelected(selectedIds.toList()) },
                    containerColor = themeColor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Check, "完成")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            if (displayList.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        if (selectedCategory == PlantCategory.Collection) "暂无收藏植物，长按植物即可收藏" else "未找到相关植物",
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 56.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayList, key = { it.id }) { plant ->
                        val isSelected = isMultiSelect && selectedIds.contains(plant.id)
                        val isFavorite = favoriteIds.contains(plant.id)

                        PlantGridItem(
                            plant = plant,
                            isSelected = isSelected,
                            isFavorite = isFavorite,
                            onClick = {
                                if (isMultiSelect) {
                                    selectedIds = if (isSelected) {
                                        selectedIds - plant.id
                                    } else {
                                        selectedIds + plant.id
                                    }
                                } else {
                                    onPlantSelected(plant.id)
                                }
                            },
                            onLongClick = {
                                PlantRepository.toggleFavorite(context, plant.id)
                                val msg = if (PlantRepository.isFavorite(plant.id)) "已加入收藏" else "已取消收藏"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantGridItem(
    plant: PlantInfo,
    isSelected: Boolean = false,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF8BC34A) else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val bgColor = if (isSelected) Color(0xFF8BC34A).copy(alpha = 0.1f) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AssetImage(
                path = if (plant.icon != null) "images/plants/${plant.icon}" else "images/others/unknown.jpg",
                contentDescription = plant.name,
                filterQuality = FilterQuality.High,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape),
                placeholder = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = plant.name.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            )

            if (isFavorite) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, CircleShape)
                        .border(0.5.dp, Color(0xFFFFC107), CircleShape)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = plant.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            maxLines = 1,
            color = Color.Black,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        Text(
            text = plant.id,
            fontSize = 8.sp,
            color = Color.Gray,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 10.sp
        )
    }
}