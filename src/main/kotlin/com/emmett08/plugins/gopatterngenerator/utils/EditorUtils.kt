package com.emmett08.plugins.gopatterngenerator.utils

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.Editor

object EditorUtils {
    fun getSelectedText(project: Project): List<String> {
        val editor = getEditor(project)
        return editor?.selectionModel?.selectedText?.split("\n")?.map { it.trim() } ?: emptyList()
    }

    fun getEditor(project: Project): Editor? {
        return EditorFactory.getInstance().allEditors.firstOrNull { it.project == project }
    }
}
