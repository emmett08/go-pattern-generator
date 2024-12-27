package com.emmett08.plugins.gopatterngenerator.actions

import com.emmett08.plugins.gopatterngenerator.template.pattern.ProxyTemplate
import com.goide.psi.GoFile
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil

class ProxyAction : BaseAction() {
    override fun actionPerformedImpl(event: AnActionEvent, project: Project, file: GoFile, editor: Editor) {
        PopupUtil.getChooseStructPopup(file, editor, project, "Choose struct to be proxy") { structType ->
            PopupUtil.getChooseInterfacePopup(file, project, "Choose which interface to implement proxy pattern") { interfaceToImpl ->
                ProxyTemplate(event, structType, interfaceToImpl).generateText()
            }
        }
    }
}
