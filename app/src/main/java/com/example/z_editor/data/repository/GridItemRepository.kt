package com.example.z_editor.data.repository

data class GridItemInfo(
    val typeName: String,
    val name: String,
    val category: GridItemCategory,
    val icon: String?
)

enum class GridItemCategory(val label: String) {
    Scene("场景布置"),
    Trap("陷阱强化")
}

/**
 * 障碍物数据仓库
 * 目前是静态数据，未来可扩展为从 GridItemTypes.json 加载
 */
object GridItemRepository {

    private val staticItems = listOf(
        GridItemInfo("gravestone_egypt", "埃及墓碑", GridItemCategory.Scene, "gravestone_egypt.png"),
        GridItemInfo("gravestone_dark", "黑暗墓碑", GridItemCategory.Scene, "gravestone_dark.png"),
        GridItemInfo("gravestoneSunOnDestruction", "阳光墓碑", GridItemCategory.Scene, "gravestoneSunOnDestruction.png"),
        GridItemInfo("gravestonePlantfoodOnDestruction", "能量豆墓碑", GridItemCategory.Scene, "gravestonePlantfoodOnDestruction.png"),

        GridItemInfo("heian_box_sun", "阳光赛钱箱", GridItemCategory.Scene, "heian_box_sun.png"),
        GridItemInfo("heian_box_plantfood", "能量豆赛钱箱", GridItemCategory.Scene, "heian_box_plantfood.png"),
        GridItemInfo("heian_box_levelup", "升级赛钱箱", GridItemCategory.Scene, "heian_box_levelup.png"),
        GridItemInfo("heian_box_seedpacket", "种子赛钱箱", GridItemCategory.Scene, "heian_box_seedpacket.png"),

        GridItemInfo("slider_up", "上行冰河浮冰", GridItemCategory.Scene, "slider_up.png"),
        GridItemInfo("slider_down", "下行冰河浮冰", GridItemCategory.Scene, "slider_down.png"),
        GridItemInfo("slider_up_modern", "上行摩登浮标", GridItemCategory.Scene, "slider_up_modern.png"),
        GridItemInfo("slider_down_modern", "下行摩登浮标", GridItemCategory.Scene, "slider_down_modern.png"),

        GridItemInfo("goldtile", "黄金地砖", GridItemCategory.Scene, "goldtile.png"),
        GridItemInfo("fake_mold", "霉菌地面", GridItemCategory.Scene, "fake_mold.png"),
        GridItemInfo("lilipad", "莲叶", GridItemCategory.Scene, "lilypad.jpg"),

        GridItemInfo("zombiepotion_speed", "疾速药水", GridItemCategory.Trap, "zombiepotion_speed.png"),
        GridItemInfo("zombiepotion_toughness", "坚韧药水", GridItemCategory.Trap, "zombiepotion_toughness.png"),
        GridItemInfo("zombiepotion_invisible", "隐身药水", GridItemCategory.Trap, "zombiepotion_invisible.png"),
        GridItemInfo("zombiepotion_poison", "剧毒药水", GridItemCategory.Trap, "zombiepotion_poison.png"),

        GridItemInfo("boulder_trap_falling_forward", "滚石陷阱", GridItemCategory.Trap, "boulder_trap_falling_forward.png"),
        GridItemInfo("flame_spreader_trap", "火焰陷阱", GridItemCategory.Trap, "flame_spreader_trap.png"),
        GridItemInfo("bufftile_shield", "护盾瓷砖", GridItemCategory.Trap, "bufftile_shield.png"),
        GridItemInfo("bufftile_speed", "疾速瓷砖", GridItemCategory.Trap, "bufftile_speed.png"),
        GridItemInfo("bufftile_attack", "攻击瓷砖", GridItemCategory.Trap, "bufftile_attack.png"),
        GridItemInfo("zombie_bound_tile", "僵尸跳板", GridItemCategory.Trap, "zombie_bound_tile.png"),
        GridItemInfo("zombie_changer", "僵尸改造机", GridItemCategory.Trap, "zombie_changer.png"),
    )

    val allItems: List<GridItemInfo>
        get() = staticItems

    private val validAliasesSet: Set<String> by lazy {
        allItems.map { it.typeName }.toSet()
    }

    // === 业务方法 ===
    fun getByCategory(category: GridItemCategory): List<GridItemInfo> {
        return allItems.filter { it.category == category }
    }

    fun getName(typeName: String): String {
        val staticName = allItems.find { it.typeName == typeName }?.name
        if (staticName != null) return staticName
        return typeName
    }

    fun getIconPath(typeName: String): String? {
        val icon = allItems.find { it.typeName == typeName }?.icon
        return if (icon != null) "images/griditems/$icon" else null
    }

    fun isValid(typeName: String): Boolean {
        if (allItems.any { it.typeName == typeName }) return true
        return ReferenceRepository.isValidGridItem(typeName)
    }

    fun search(query: String): List<GridItemInfo> {
        if (query.isBlank()) return allItems
        return allItems.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.typeName.contains(query, ignoreCase = true)
        }
    }
}