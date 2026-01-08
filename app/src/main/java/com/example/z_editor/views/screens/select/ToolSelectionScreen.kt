package com.example.z_editor.views.screens.select

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.z_editor.data.repository.ToolCardInfo
import com.example.z_editor.data.repository.ToolRepository
import com.example.z_editor.views.components.AssetImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolSelectionScreen(
    onToolSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val themeColor = Color(0xFF009688)

    val toolCards = remember { ToolRepository.getAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选择工具卡", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(toolCards) { tool ->
                    ToolGridItem(tool = tool) {
                        onToolSelected(tool.id)
                    }
                }
            }
        }
    }
}

@Composable
fun ToolGridItem(tool: ToolCardInfo, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val iconPath = remember(tool.icon) {
            if (!tool.icon.isNullOrEmpty()) "images/tools/${tool.icon}" else null
        }

        AssetImage(
            path = iconPath,
            contentDescription = tool.name,
            filterQuality = FilterQuality.Medium,
            modifier = Modifier
                .size(height = 44.dp, width = 56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE))
                .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            placeholder = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tool.name.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = tool.name,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = tool.id,
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}