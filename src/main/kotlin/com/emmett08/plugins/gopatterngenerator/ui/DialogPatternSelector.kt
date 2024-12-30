package com.emmett08.plugins.gopatterngenerator.ui

import com.emmett08.plugins.gopatterngenerator.core.PatternSelector
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*
import java.awt.*

class DropdownPatternSelector(project: Project) : DialogWrapper(project), PatternSelector {
    private val options = arrayOf("Singleton", "Factory", "Builder")
    private val comboBox = ComboBox(options)

    init {
        init()
        title = "Generate Go Pattern"
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(BorderLayout()).apply {
            add(JLabel("Choose a design pattern to generate:"), BorderLayout.NORTH)
            add(comboBox, BorderLayout.CENTER)
        }
    }

    override fun selectPattern(project: Project): String? {
        val dialog = DropdownPatternSelector(project)
        return if (dialog.showAndGet()) dialog.comboBox.selectedItem as String? else null
    }
}
