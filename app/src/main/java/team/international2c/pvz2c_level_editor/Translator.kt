package team.international2c.pvz2c_level_editor

import android.content.Context

object Translator {
    fun t(context: Context, key: String): String {
        val resources = context.resources
        val resourceId = resources.getIdentifier(key, "string", context.packageName)
        return if (resourceId != 0) {
            resources.getString(resourceId)
        } else {
            key
        }
    }
}
