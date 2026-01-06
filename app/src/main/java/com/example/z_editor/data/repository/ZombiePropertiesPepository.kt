package com.example.z_editor.data.repository

import android.content.Context
import com.example.z_editor.data.PvzLevelFile
import com.example.z_editor.data.PvzObject
import com.example.z_editor.data.RtidParser
import com.example.z_editor.data.ZombiePropertySheetData
import com.example.z_editor.data.ZombieStats
import com.example.z_editor.data.ZombieTypeData
import com.google.gson.Gson
import java.io.InputStreamReader

object ZombiePropertiesRepository {
    private val gson = Gson()

    private val statsCache = mutableMapOf<String, ZombieStats>()

    private val aliasToTypeCache = mutableMapOf<String, String>()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        try {
            val propsFileMap = loadReferenceFile(context, "reference/PropertySheets.json")

            val typesFileMap = loadReferenceFile(context, "reference/ZombieTypes.json")

            typesFileMap.forEach { (alias, typeObj) ->
                try {
                    val typeData = gson.fromJson(typeObj.objData, ZombieTypeData::class.java)
                    val typeName = typeData.typeName

                    if (typeName.isNotBlank()) {
                        aliasToTypeCache[alias] = typeName
                        aliasToTypeCache[typeName] = typeName

                        val propsAlias = RtidParser.parse(typeData.properties)?.alias ?: ""
                        val propsObj = propsFileMap[propsAlias]
                        if (propsObj != null) {
                            val sheet = gson.fromJson(propsObj.objData, ZombiePropertySheetData::class.java)
                            val stats = ZombieStats(
                                id = typeName,
                                hp = sheet.hitpoints,
                                cost = sheet.wavePointCost,
                                weight = sheet.weight.toInt(),
                                speed = sheet.speed,
                                eatDPS = sheet.eatDPS,
                                sizeType = sheet.sizeType
                            )
                            statsCache[typeName] = stats
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadReferenceFile(context: Context, path: String): Map<String, PvzObject> {
        return try {
            val inputStream = context.assets.open(path)
            val root = gson.fromJson(InputStreamReader(inputStream), PvzLevelFile::class.java)
            root.objects.associateBy { it.aliases?.firstOrNull() ?: "unknown" }
        } catch (e: Exception) {
            println("Error loading $path: ${e.message}")
            emptyMap()
        }
    }

    fun getTypeNameByAlias(alias: String): String = aliasToTypeCache[alias] ?: alias

    fun getStats(typeName: String): ZombieStats =
        statsCache[typeName] ?: ZombieStats(typeName, 0.0, 0, 0, 0.0, 0.0, "unknown")

    fun isValidAlias(alias: String): Boolean {
        return aliasToTypeCache.containsKey(alias)
    }
}