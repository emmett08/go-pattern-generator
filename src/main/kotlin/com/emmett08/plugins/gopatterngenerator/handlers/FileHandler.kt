package com.emmett08.plugins.gopatterngenerator.handlers

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.ui.Messages
import java.io.IOException

class FileHandler {
    fun writeToFile(baseDir: com.intellij.openapi.vfs.VirtualFile?, content: String, project: com.intellij.openapi.project.Project) {
        WriteAction.run<IOException> {
            try {
                val file = baseDir?.createChildData(this, "pattern.go")
                file?.setBinaryContent(content.toByteArray())
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Failed to create file", "Error")
            }
        }
    }
}