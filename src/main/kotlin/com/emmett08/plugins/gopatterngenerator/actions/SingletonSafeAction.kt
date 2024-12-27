package com.emmett08.plugins.gopatterngenerator.actions

import com.goide.psi.GoFile
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.emmett08.plugins.gopatterngenerator.template.pattern.SingletonTemplate
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil

class SingletonSafeAction : BaseAction() {
    override fun actionPerformedImpl(event: AnActionEvent, project: Project, file: GoFile, editor: Editor) {
        PopupUtil.getChooseStructPopup(file, editor, project, null) { goTypeSpec ->
            SingletonTemplate(event, goTypeSpec, true).generateText()
        }
    }
}
