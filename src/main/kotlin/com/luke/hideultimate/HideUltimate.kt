package com.luke.hideultimate

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.IdeFrame

/**
 * Unregisters the TrialStateWidget action as early as possible.
 *
 * Uses multiple hooks to ensure the action is removed:
 * 1. AppLifecycleListener.appStarted() — fires when the IDE application starts (earliest)
 * 2. ApplicationActivationListener — fires when the IDE window gets focus
 * 3. ProjectActivity — fires after project opens (backup)
 */
object HideUtil {
    @Volatile
    private var done = false

    fun hideTrialWidget() {
        if (done) return
        val actionManager = ActionManager.getInstance()
        val trialAction = actionManager.getAction("TrialStateWidget")
        if (trialAction != null) {
            actionManager.unregisterAction("TrialStateWidget")
            done = true
        }
    }
}

class HideUltimateLifecycleListener : AppLifecycleListener {
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        HideUtil.hideTrialWidget()
    }
}

class HideUltimateStartup : ProjectActivity {
    override suspend fun execute(project: Project) {
        HideUtil.hideTrialWidget()
    }
}

class HideUltimateAppListener : ApplicationActivationListener {
    override fun applicationActivated(ideFrame: IdeFrame) {
        HideUtil.hideTrialWidget()
    }
}
