package co.tuister.uisers.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import co.tuister.uisers.R
import co.tuister.uisers.modules.login.LoginActivity

class ShortcutsManager(val context: Context) {

    fun enableShortcuts(subjects: Boolean = true, tasks: Boolean = true) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return

        val shortcuts = mutableListOf<ShortcutInfo>()

        if (subjects) {
            shortcuts.add(createSubjectsShortcuts())
        }

        if (tasks) {
            shortcuts.add(createTasksShortcuts())
        }

        val shortcutManager: ShortcutManager =
            context.getSystemService(ShortcutManager::class.java)

        shortcutManager.addDynamicShortcuts(shortcuts)
    }

    fun disableShortcuts(subjects: Boolean = true, tasks: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val shortcuts = mutableListOf<String>()
        if (subjects) { shortcuts.add(SUBJECTS_SHORTCUT_ID) }
        if (tasks) { shortcuts.add(TASKS_SHORTCUT_ID) }

        shortcutManager.disableShortcuts(shortcuts)
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun createSubjectsShortcuts(): ShortcutInfo {
        val newTaskIntent = LoginActivity.createIntent(context, context.getString(R.string.deep_link_career)).apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return ShortcutInfo.Builder(context, SUBJECTS_SHORTCUT_ID)
            .setShortLabel(context.getString(R.string.title_menu_subjects))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_subject))
            .setIntent(newTaskIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun createTasksShortcuts(): ShortcutInfo {

        val newTaskIntent = LoginActivity.createIntent(context, context.getString(R.string.deep_link_tasks)).apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return ShortcutInfo.Builder(context, TASKS_SHORTCUT_ID)
            .setShortLabel(context.getString(R.string.title_menu_task))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_task))
            .setIntent(newTaskIntent)
            .build()
    }

    companion object {
        private const val TASKS_SHORTCUT_ID = "333"
        private const val SUBJECTS_SHORTCUT_ID = "444"
    }
}
