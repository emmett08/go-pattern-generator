package com.emmett08.plugins.gopatterngenerator.actions

import com.goide.psi.GoFile
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.emmett08.plugins.gopatterngenerator.template.pattern.FactoryTemplate
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil
import com.emmett08.plugins.gopatterngenerator.utils.NotificationUtil

class FactoryAction : BaseAction() {
    override fun actionPerformedImpl(event: AnActionEvent, project: Project, file: GoFile, editor: Editor) {
        PopupUtil.getMultiChooseStructPopup(file, editor, project, null) { list ->
            when {
                list == null -> NotificationUtil.notifyWarn(project, "Please select at least 2 structures!\nTip: Use Shift+(Left Click)")
                list.isEmpty() -> NotificationUtil.notifyWarn(project, "Please right click at blank line and select")
                else -> PopupUtil.getChooseInterfacePopup(file, project, null) { interfaceToImpl ->
                    FactoryTemplate(event, list, interfaceToImpl).generateText()
                }
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
