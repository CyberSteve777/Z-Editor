package team.international2c.pvz2c_level_editor.views.editor.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.international2c.pvz2c_level_editor.data.EvilDavePropertiesData
import team.international2c.pvz2c_level_editor.data.ParsedLevelData
import team.international2c.pvz2c_level_editor.data.PvzLevelFile
import team.international2c.pvz2c_level_editor.views.editor.pages.others.StepperControl
import com.google.gson.Gson

private val gson = Gson()

@Composable
fun IZombieTab(
    rootLevelFile: PvzLevelFile?,
    parsedData: ParsedLevelData?
) {
    if (rootLevelFile == null || parsedData == null) return

    val evilDaveObj = remember(rootLevelFile.objects) {
        rootLevelFile.objects.find { it.objClass == "EvilDaveProperties" }
    }

    if (evilDaveObj == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("数据异常：未找到我是僵尸配置模块")
        }
        return
    }

    val dataState = remember(evilDaveObj) {
        val initialData = try {
            gson.fromJson(evilDaveObj.objData, EvilDavePropertiesData::class.java)
        } catch (_: Exception) {
            EvilDavePropertiesData()
        }
        mutableStateOf(initialData)
    }

    fun sync() {
        evilDaveObj.objData = gson.toJsonTree(dataState.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val currentDist = dataState.value.plantDistance
        StepperControl(
            label = "植物预留列 (PlantDistance)",
            valueText = "第 $currentDist 列",
            onMinus = {
                val newVal = (currentDist - 1).coerceAtLeast(0)
                if (newVal != currentDist) {
                    dataState.value = dataState.value.copy(plantDistance = newVal)
                    sync()
                }
            },
            onPlus = {
                val newVal = (currentDist + 1).coerceAtMost(9)
                if (newVal != currentDist) {
                    dataState.value = dataState.value.copy(plantDistance = newVal)
                    sync()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF5E5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Info, null, tint = Color(0xFF388E3C))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "我是僵尸模式下的预置植物和僵尸选择分别要在关卡模块里的预置植物和种子库里配置。",
                        fontSize = 12.sp,
                        color = Color(0xFF388E3C),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}