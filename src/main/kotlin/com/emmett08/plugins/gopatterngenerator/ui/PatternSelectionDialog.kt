package com.emmett08.plugins.gopatterngenerator.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class PatternSelectionDialog {
    private val options = arrayOf("Singleton", "Factory", "Builder")

    fun show(project: Project): String? {
        val selection = Messages.showDialog(
            project,
            "Choose a design pattern to generate:",
            "Generate Go Pattern",
            options,
            0,
            Messages.getQuestionIcon()
        )
        return if (selection == -1) null else options[selection]
    }
}
