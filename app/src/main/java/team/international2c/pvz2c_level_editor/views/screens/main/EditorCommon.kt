package team.international2c.pvz2c_level_editor.views.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import team.international2c.pvz2c_level_editor.R
import team.international2c.pvz2c_level_editor.data.EditorSubScreen
import team.international2c.pvz2c_level_editor.data.EventMetadata
import team.international2c.pvz2c_level_editor.data.ModuleMetadata
import team.international2c.pvz2c_level_editor.data.SunDropperPropertiesData
import team.international2c.pvz2c_level_editor.data.repository.ChallengeTypeInfo

/**
 * 编辑器的一级 Tab 类型定义
 */
enum class EditorTabType(val titleResId: Int, val icon: ImageVector) {
    Settings(R.string.level_settings, Icons.Default.Settings),
    Timeline(R.string.wave_containers, Icons.Default.Inbox),
    IZombie(R.string.i_zombie, Icons.Default.EmojiPeople),
    VaseBreaker(R.string.vase_layout, Icons.Default.Grid4x4),
    BossFight(R.string.zomboss_properties, Icons.Default.Dangerous),
}

/**
 * 定义所有编辑器操作的回调集合
 * 用于解耦 EditorScreen 的逻辑与 EditorContentRouter 的 UI
 */
data class EditorActions(
    val navigateTo: (EditorSubScreen) -> Unit,
    val navigateBack: () -> Unit,

    val onRemoveModule: (String) -> Unit,
    val onAddModule: (ModuleMetadata) -> Unit,
    val onAddEvent: (EventMetadata, Int) -> Unit,
    val onWavesChanged: () -> Unit,
    val onLevelDefChanged: () -> Unit,
    val onDeleteEventReference: (String) -> Unit,
    val onSaveWaveManager: () -> Unit,
    val onCreateWaveContainer: () -> Unit,
    val onDeleteWaveContainer: () -> Unit,
    val onStageSelected: (String) -> Unit,
    val onStageCanceled: () -> Unit,

    val onAddChallenge: (ChallengeTypeInfo) -> Unit,
    val onInjectZombie: (String) -> String?,
    val onEditCustomZombie: (String) -> Unit,

    val onLaunchPlantSelector: ((String) -> Unit) -> Unit,
    val onLaunchZombieSelector: ((String) -> Unit) -> Unit,
    val onLaunchMultiPlantSelector: ((List<String>) -> Unit) -> Unit,
    val onLaunchMultiZombieSelector: ((List<String>) -> Unit) -> Unit,
    val onLaunchGridItemSelector: ((String) -> Unit) -> Unit,
    val onLaunchChallengeSelector: ((ChallengeTypeInfo) -> Unit) -> Unit,
    val onLaunchToolSelector: ((String) -> Unit) -> Unit,
    val onLaunchZombossSelector: ((String) -> Unit) -> Unit,

    val onSelectorResult: (Any) -> Unit,
    val onSelectorCancel: () -> Unit,

    val onToggleSunDropperMode: (Boolean, SunDropperPropertiesData) -> Unit = { _, _ -> },
    val onChallengeSelected: (ChallengeTypeInfo) -> Unit
)
