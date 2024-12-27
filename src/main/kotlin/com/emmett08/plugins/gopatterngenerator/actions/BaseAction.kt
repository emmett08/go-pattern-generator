package com.emmett08.plugins.gopatterngenerator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.emmett08.plugins.gopatterngenerator.utils.NotificationUtil
import com.goide.psi.GoFile

abstract class BaseAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(LangDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = project != null && file is GoFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(LangDataKeys.PSI_FILE) as? GoFile
        val editor = e.getData(LangDataKeys.EDITOR)

        if (file === null || project == null || editor == null) {
            project?.let {
                NotificationUtil.notifyError(it, "Can't generate content, because file is not correct")
            }
            return
        }

        WriteCommandAction.runWriteCommandAction(project) {
            actionPerformedImpl(e, project, file, editor)
        }
    }

    protected abstract fun actionPerformedImpl(event: AnActionEvent, project: Project, file: GoFile, editor: Editor)
}
