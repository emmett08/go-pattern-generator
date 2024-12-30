package com.emmett08.plugins.gopatterngenerator.handlers

import com.emmett08.plugins.gopatterngenerator.utils.EditorUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages

class FileHandler {
    fun overwriteSelectedCode(content: String, project: com.intellij.openapi.project.Project) {
        val editor = EditorUtils.getEditor(project) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            val selectionModel = editor.selectionModel
            if (selectionModel.hasSelection()) {
                editor.document.replaceString(selectionModel.selectionStart, selectionModel.selectionEnd, content)
            } else {
                Messages.showErrorDialog(project, "No code selected for refactoring", "Error")
            }
        }
    }
}
