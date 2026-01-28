package team.international2c.pvz2c_level_editor.views.screens.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import team.international2c.pvz2c_level_editor.data.repository.FileItem
import team.international2c.pvz2c_level_editor.data.repository.LevelRepository
import team.international2c.pvz2c_level_editor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Breadcrumb(val name: String, val uri: Uri)

class OpenDocumentTreeFixed : ActivityResultContract<Uri?, Uri?>() {
    override fun createIntent(context: Context, input: Uri?): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        if (input != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
        } else {
            val primaryRootUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A")
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, primaryRootUri)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == android.app.Activity.RESULT_OK) intent?.data else null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelListScreen(
    onLevelClick: (String, Uri) -> Unit,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ======================== 1. 状态声明 ========================

    // 当前目录的内容列表
    val fileItems = remember { mutableStateListOf<FileItem>() }
    var isLoading by remember { mutableStateOf(false) }

    // 路径栈：用于面包屑导航和返回上一级
    var pathStack by remember { mutableStateOf(listOf<Breadcrumb>()) }

    // 获取根目录 Uri (从 SharedPreferences)
    var rootFolderUri by remember {
        mutableStateOf(
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getString("folder_uri", null)?.toUri()
        )
    }

    var itemToMove by remember { mutableStateOf<FileItem?>(null) }
    var moveSourceUri by remember { mutableStateOf<Uri?>(null) }
    val isMovingMode = itemToMove != null

    // 各种弹窗状态
    var showNoFolderDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FileItem?>(null) }
    var itemToRename by remember { mutableStateOf<FileItem?>(null) }
    var itemToCopy by remember { mutableStateOf<FileItem?>(null) }

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var newFolderNameInput by remember { mutableStateOf("") }

    var showTemplateDialog by remember { mutableStateOf(false) }
    var showCreateNameDialog by remember { mutableStateOf(false) }

    var showMenu by remember { mutableStateOf(false) }
    var confirmCheckbox by remember { mutableStateOf(false) }

    // 输入框临时变量
    var renameInput by remember { mutableStateOf("") }
    var copyInput by remember { mutableStateOf("") }
    var newLevelNameInput by remember { mutableStateOf("") }

    var templates by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTemplate by remember { mutableStateOf("") }

    // ======================== 2. 核心逻辑 ========================

    fun loadCurrentDirectory() {
        val currentUri = pathStack.lastOrNull()?.uri ?: rootFolderUri ?: return

        isLoading = true
        scope.launch {
            val items = withContext(Dispatchers.IO) {
                LevelRepository.getDirectoryContents(context, currentUri)
            }
            fileItems.clear()
            fileItems.addAll(items)
            isLoading = false
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = OpenDocumentTreeFixed()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .edit { putString("folder_uri", uri.toString()) }

            rootFolderUri = uri

            val docFile = DocumentFile.fromTreeUri(context, uri)
            val rootName = docFile?.name ?: context.getString(R.string.root_directory)
            pathStack = listOf(Breadcrumb(rootName, uri))

            showNoFolderDialog = false
            loadCurrentDirectory()
        }
    }

    LaunchedEffect(Unit) {
        if (rootFolderUri == null) {
            showNoFolderDialog = true
        } else {
            if (pathStack.isEmpty()) {
                val docFile = DocumentFile.fromTreeUri(context, rootFolderUri!!)
                val rootName = docFile?.name ?: context.getString(R.string.root_directory)
                pathStack = listOf(Breadcrumb(rootName, rootFolderUri!!))
            }
            loadCurrentDirectory()
        }
    }

    // 返回键处理
    BackHandler(enabled = pathStack.size > 1) {
        pathStack = pathStack.dropLast(1)
        loadCurrentDirectory()
    }

    // --- 文件/文件夹 操作逻辑 ---
    fun navigateToFolder(folder: FileItem) {
        pathStack = pathStack + Breadcrumb(folder.name, folder.uri)
        loadCurrentDirectory()
    }

    fun handleRenameConfirm() {
        val target = itemToRename ?: return
        val currentUri = pathStack.last().uri
        var finalName = renameInput.trim()
        if (!target.isDirectory && !finalName.endsWith(".json", ignoreCase = true)) {
            finalName += ".json"
        }
        if (LevelRepository.renameItem(
                context,
                currentUri,
                target.name,
                finalName,
                target.isDirectory
            )
        ) {
            Toast.makeText(context, context.getString(R.string.rename_success), Toast.LENGTH_SHORT).show()
            itemToRename = null
            loadCurrentDirectory()
        } else {
            Toast.makeText(context, context.getString(R.string.rename_failed_same_name), Toast.LENGTH_SHORT).show()
        }
    }

    fun handleDeleteConfirm() {
        val target = itemToDelete ?: return
        val currentUri = pathStack.last().uri

        LevelRepository.deleteItem(context, currentUri, target.name, target.isDirectory)
        Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
        itemToDelete = null
        loadCurrentDirectory()
    }

    fun handleCopyConfirm() {
        val target = itemToCopy ?: return
        val currentUri = pathStack.last().uri

        var finalName = copyInput.trim()
        if (!finalName.endsWith(".json", ignoreCase = true)) {
            finalName += ".json"
        }
        if (LevelRepository.copyLevelToTarget(context, target.name, finalName, currentUri)) {
            Toast.makeText(context, context.getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
            itemToCopy = null
            loadCurrentDirectory()
        } else {
            Toast.makeText(context, context.getString(R.string.copy_failed), Toast.LENGTH_SHORT).show()
        }
    }

    fun handleMoveConfirm() {
        val target = itemToMove ?: return
        val source = moveSourceUri ?: return
        val dest = pathStack.last().uri

        if (source == dest) {
            Toast.makeText(context, context.getString(R.string.same_source_dest), Toast.LENGTH_SHORT).show()
            itemToMove = null
            moveSourceUri = null
            return
        }

        isLoading = true
        scope.launch {
            val success = withContext(Dispatchers.IO) {
                LevelRepository.moveFile(context, source, target.name, dest)
            }
            isLoading = false
            if (success) {
                Toast.makeText(context, context.getString(R.string.move_success), Toast.LENGTH_SHORT).show()
                itemToMove = null
                moveSourceUri = null
                loadCurrentDirectory()
            } else {
                Toast.makeText(context, context.getString(R.string.move_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleNewFolder() {
        if (newFolderNameInput.isBlank()) return
        val currentUri = pathStack.last().uri
        if (LevelRepository.createDirectory(context, currentUri, newFolderNameInput)) {
            Toast.makeText(context, context.getString(R.string.folder_create_success), Toast.LENGTH_SHORT).show()
            showNewFolderDialog = false
            newFolderNameInput = ""
            loadCurrentDirectory()
        } else {
            Toast.makeText(context, context.getString(R.string.create_failed), Toast.LENGTH_SHORT).show()
        }
    }

    fun openTemplateSelector() {
        templates = LevelRepository.getTemplateList(context)
        if (templates.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.template_not_found), Toast.LENGTH_SHORT).show()
        } else {
            showTemplateDialog = true
        }
    }

    fun handleCreateLevelConfirm() {
        val currentUri = pathStack.lastOrNull()?.uri ?: return
        var name = newLevelNameInput
        if (!name.endsWith(".json", true)) name += ".json"

        if (LevelRepository.createLevelFromTemplate(context, currentUri, selectedTemplate, name)) {
            Toast.makeText(context, context.getString(R.string.create_success), Toast.LENGTH_SHORT).show()
            showCreateNameDialog = false
            loadCurrentDirectory()
        } else {
            Toast.makeText(context, context.getString(R.string.create_failed_same_name), Toast.LENGTH_SHORT).show()
        }
    }

    // ======================== 3. UI 渲染 ========================

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.my_level_library), fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = { loadCurrentDirectory() }) {
                        Icon(
                            Icons.Default.Refresh,
                            context.getString(R.string.refresh)
                        )
                    }
                    IconButton(onClick = { folderPickerLauncher.launch(null) }) {
                        Icon(
                            Icons.Default.FolderOpen,
                            context.getString(R.string.switch_root)
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, context.getString(R.string.more_options), tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(context.getString(R.string.clear_cache)) },
                                onClick = {
                                    showMenu = false
                                    val count = LevelRepository.clearAllInternalCache(context)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.cleared_cache_count, count),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, null, tint = Color.Gray)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(context.getString(R.string.about_software)) },
                                onClick = {
                                    showMenu = false
                                    onAboutClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, null, tint = Color.Gray)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (isMovingMode) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            itemToMove = null
                            moveSourceUri = null
                        },
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFD32F2F),
                        icon = { Icon(Icons.Default.Close, null) },
                        text = { Text(context.getString(R.string.cancel)) }
                    )
                    ExtendedFloatingActionButton(
                        onClick = { handleMoveConfirm() },
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.ContentPaste, null) },
                        text = { Text(context.getString(R.string.paste)) }
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { showNewFolderDialog = true },
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF2E7D32),
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(Icons.Default.CreateNewFolder, context.getString(R.string.new_folder))
                    }

                    FloatingActionButton(
                        onClick = { openTemplateSelector() },
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(Icons.Default.Add, context.getString(R.string.new_level))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // --- 面包屑导航栏 ---
            BreadcrumbBar(
                pathStack = pathStack,
                onBreadcrumbClick = { index ->
                    pathStack = pathStack.take(index + 1)
                    loadCurrentDirectory()
                }
            )
            if (isMovingMode) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DriveFileMove, null, tint = Color(0xFF1976D2))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "正在移动: ${itemToMove?.name}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                fontSize = 14.sp
                            )
                            Text(
                                "请导航至目标文件夹，然后点击右下角粘贴",
                                fontSize = 12.sp,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (pathStack.size > 1) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        pathStack = pathStack.dropLast(1)
                                        loadCurrentDirectory()
                                    },
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Folder,
                                        null,
                                        tint = Color.Gray
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        context.getString(R.string.back_to_previous),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    if (fileItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.FolderOpen,
                                        null,
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(context.getString(R.string.empty_folder), color = Color.Gray)
                                }
                            }
                        }
                    } else {
                        // C. 文件列表
                        items(fileItems) { item ->
                            val isSelfMoving = isMovingMode && itemToMove == item
                            val isInteractionDisabled = isMovingMode && !item.isDirectory
                            val isActionButtonsDisabled = isMovingMode

                            val alpha = if (isInteractionDisabled || isSelfMoving) 0.5f else 1f

                            FileItemRow(
                                item = item,
                                modifier = Modifier.alpha(alpha),
                                onClick = {
                                    if (isMovingMode) {
                                        if (item.isDirectory) navigateToFolder(item)
                                    } else {
                                        if (item.isDirectory) {
                                            navigateToFolder(item)
                                        } else {
                                            if (LevelRepository.prepareInternalCache(
                                                    context,
                                                    item.uri,
                                                    item.name
                                                )
                                            ) {
                                                onLevelClick(item.name, item.uri)
                                            }
                                        }
                                    }
                                },
                                actionsEnabled = !isActionButtonsDisabled, onRename = {
                                    renameInput = if (item.isDirectory) {
                                        item.name
                                    } else {
                                        item.name.substringBeforeLast(".")
                                    }
                                    itemToRename = item
                                },
                                onDelete = { itemToDelete = item },
                                onCopy = {
                                    if (!item.isDirectory) {
                                        val base = item.name.substringBeforeLast(".")
                                        copyInput = "${base}_copy"
                                        itemToCopy = item
                                    }
                                },
                                onMove = {
                                    if (!item.isDirectory) {
                                        itemToMove = item
                                        moveSourceUri = pathStack.last().uri
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(160.dp)) }
                }
            }
        }
    }

    if (showNoFolderDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(context.getString(R.string.initial_setup)) },
            text = { Text(context.getString(R.string.select_folder_for_levels)) },
            confirmButton = { Button(onClick = { folderPickerLauncher.launch(null) }) { Text(context.getString(R.string.choose_folder)) } }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(context.getString(R.string.confirm_delete)) },
            text = {
                Column {
                    Text(context.getString(R.string.confirm_delete_item, itemToDelete?.name ?: "") + "\n" + if (itemToDelete!!.isDirectory) context.getString(R.string.folder_delete_warning) else context.getString(R.string.irreversible_action))
                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.05f))
                            .clickable { confirmCheckbox = !confirmCheckbox }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = confirmCheckbox,
                            onCheckedChange = { confirmCheckbox = it }
                        )
                        Text(
                            if (itemToDelete!!.isDirectory) context.getString(R.string.confirm_permanent_delete_folder) else context.getString(R.string.confirm_permanent_delete_level),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        handleDeleteConfirm()
                        confirmCheckbox = false
                    },
                    enabled = confirmCheckbox,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text(context.getString(R.string.confirm_delete)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    itemToDelete = null
                    confirmCheckbox = false
                }) { Text(context.getString(R.string.cancel)) }
            }
        )
    }

    if (itemToRename != null) {
        AlertDialog(
            onDismissRequest = { itemToRename = null },
            title = { Text(context.getString(R.string.rename)) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    label = { Text(context.getString(R.string.new_name)) },
                    singleLine = true
                )
            },
            confirmButton = { Button(onClick = { handleRenameConfirm() }) { Text(context.getString(R.string.confirm)) } },
            dismissButton = { TextButton(onClick = { itemToRename = null }) { Text(context.getString(R.string.cancel)) } }
        )
    }

    if (itemToCopy != null) {
        AlertDialog(
            onDismissRequest = { itemToCopy = null },
            title = { Text(context.getString(R.string.copy_level)) },
            text = {
                OutlinedTextField(
                    value = copyInput,
                    onValueChange = { copyInput = it },
                    label = { Text(context.getString(R.string.new_filename)) })
            },
            confirmButton = { Button(onClick = { handleCopyConfirm() }) { Text(context.getString(R.string.copy)) } },
            dismissButton = { TextButton(onClick = { itemToCopy = null }) { Text(context.getString(R.string.cancel)) } }
        )
    }

    if (showNewFolderDialog) {
        AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            title = { Text(context.getString(R.string.new_folder)) },
            text = {
                OutlinedTextField(
                    value = newFolderNameInput,
                    onValueChange = { newFolderNameInput = it },
                    label = { Text(context.getString(R.string.folder_name)) })
            },
            confirmButton = { Button(onClick = { handleNewFolder() }) { Text(context.getString(R.string.create)) } },
            dismissButton = {
                TextButton(onClick = {
                    showNewFolderDialog = false
                }) { Text(context.getString(R.string.cancel)) }
            }
        )
    }

    if (showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateDialog = false },
            title = { Text(context.getString(R.string.new_level_choose_template)) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(templates) { template ->
                        Card(
                            onClick = {
                                selectedTemplate = template
                                newLevelNameInput = template.substringBeforeLast(".")
                                showTemplateDialog = false
                                showCreateNameDialog = true
                            },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    null,
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = template.substringBeforeLast("."),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTemplateDialog = false }) { Text(context.getString(R.string.cancel)) }
            }
        )
    }

    if (showCreateNameDialog) {
        AlertDialog(
            onDismissRequest = { showCreateNameDialog = false },
            title = { Text(context.getString(R.string.name_level)) },
            text = {
                OutlinedTextField(
                    value = newLevelNameInput,
                    onValueChange = { newLevelNameInput = it })
            },
            confirmButton = { Button(onClick = { handleCreateLevelConfirm() }) { Text(context.getString(R.string.create)) } },
            dismissButton = {
                TextButton(onClick = {
                    showCreateNameDialog = false
                }) { Text(context.getString(R.string.cancel)) }
            }
        )
    }
}

// === 自定义组件 ===

@Composable
fun BreadcrumbBar(
    pathStack: List<Breadcrumb>,
    onBreadcrumbClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 6.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(pathStack.size) { index ->
            val item = pathStack[index]
            val isLast = index == pathStack.size - 1

            Surface(
                color = if (isLast) Color(0xFFE8F5E9) else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = !isLast) { onBreadcrumbClick(index) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    if (index == 0) {
                        Icon(
                            Icons.Default.FolderOpen,
                            null,
                            tint = if (isLast) Color(0xFF2E7D32) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }

                    Text(
                        text = item.name,
                        color = if (isLast) Color(0xFF2E7D32) else Color(0xFF424242),
                        fontWeight = if (isLast) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }

            if (!isLast) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun FileItemRow(
    item: FileItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    actionsEnabled: Boolean = true,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (item.isDirectory) Color(0xFFFFC107) else Color(0xFF4CAF50),
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (item.isDirectory) item.name else item.name.substringBeforeLast("."),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (!item.isDirectory) {
                    Text("JSON 文件", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (actionsEnabled) {
                Row {
                    IconButton(onClick = onRename, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!item.isDirectory) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.ContentCopy,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onMove, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.AutoMirrored.Filled.DriveFileMove,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
