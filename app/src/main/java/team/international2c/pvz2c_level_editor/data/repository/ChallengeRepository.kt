package team.international2c.pvz2c_level_editor.data.repository

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import team.international2c.pvz2c_level_editor.Translator
import team.international2c.pvz2c_level_editor.data.*

/**
 * 挑战元数据模型
 */
data class ChallengeTypeInfo(
    val title: String,          // 显示名称
    val objClass: String,       // Pvz2 类名
    val defaultAlias: String,   // 默认别名前缀
    val description: String,    // 详细描述
    val icon: ImageVector,      // UI图标
    val initialDataFactory: () -> Any = { Any() } // 默认数据生成器
)

object ChallengeRepository {

    /**
     * 获取所有挑战列表，需要 Context
     */
    fun getAllChallenges(context: Context): List<ChallengeTypeInfo> {
        return listOf(
            // First challenge uses Translator + context
            ChallengeTypeInfo(
                title = Translator.t(context, "challenge_title_beat_level"),
                objClass = "StarChallengeBeatTheLevelProps",
                defaultAlias = Translator.t(context, "challenge_alias_beat_level"),
                description = Translator.t(context, "challenge_description_beat_level"),
                icon = Icons.Default.Campaign,
                initialDataFactory = { StarChallengeBeatTheLevelData() }
            ),
            // All other challenges keep hardcoded Chinese for now
            ChallengeTypeInfo(
                title = "不丢车挑战",
                objClass = "StarChallengeSaveMowersProps",
                defaultAlias = "SaveMowers",
                description = "通关时所有小推车必须完好无损",
                icon = Icons.Default.CleaningServices,
                initialDataFactory = { StarChallengeSaveMowerData() }
            ),
            ChallengeTypeInfo(
                title = "禁用能量豆挑战",
                objClass = "StarChallengePlantFoodNonuseProps",
                defaultAlias = "PlantfoodNonuse",
                description = "关卡过程中禁止使用能量豆",
                icon = Icons.Default.Eco,
                initialDataFactory = { StarChallengePlantFoodNonuseData() }
            ),
            ChallengeTypeInfo(
                title = "幸存植物挑战",
                objClass = "StarChallengePlantsSurviveProps",
                defaultAlias = "PlantsSurive",
                description = "需要指定数量的植物在游戏结束时存活",
                icon = Icons.Default.Security,
                initialDataFactory = { StarChallengePlantSurviveData() }
            ),
            ChallengeTypeInfo(
                title = "花坛线挑战",
                objClass = "StarChallengeZombieDistanceProps",
                defaultAlias = "ZombieDistance",
                description = "不能让僵尸踩踏到花坛线",
                icon = Icons.Default.DoNotStep,
                initialDataFactory = { StarChallengeZombieDistanceData() }
            ),
            ChallengeTypeInfo(
                title = "生产阳光挑战",
                objClass = "StarChallengeSunProducedProps",
                defaultAlias = "SunProduced",
                description = "关卡结束前生产一定数量阳光",
                icon = Icons.Default.WbSunny,
                initialDataFactory = { StarChallengeSunProducedData() }
            )
            // Add remaining challenges similarly...
        )
    }

    /**
     * 搜索挑战
     */
    fun search(query: String, context: Context? = null): List<ChallengeTypeInfo> {
        val allChallenges = if (context != null) getAllChallenges(context) else getAllChallenges(context = throw Exception("Context required for Translator"))
        if (query.isBlank()) return allChallenges

        val lowerQ = query.lowercase()
        return allChallenges.filter {
            it.title.lowercase().contains(lowerQ) ||
                    it.objClass.lowercase().contains(lowerQ) ||
                    it.defaultAlias.lowercase().contains(lowerQ)
        }
    }

    /**
     * 根据 ObjClass 获取信息
     */
    fun getInfo(objClass: String, context: Context? = null): ChallengeTypeInfo? {
        val allChallenges = if (context != null) getAllChallenges(context) else getAllChallenges(context = throw Exception("Context required for Translator"))
        return allChallenges.find { it.objClass == objClass }
    }
}
