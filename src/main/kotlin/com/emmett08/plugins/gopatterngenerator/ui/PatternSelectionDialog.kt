package com.emmett08.plugins.gopatterngenerator.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import javax.swing.*

class PatternSelectionDialog(private val structName: String) : DialogWrapper(true) {

    private val patternOptions = listOf("Singleton", "Factory", "Builder")
    private val patternList = JBList(patternOptions.toTypedArray())
    private var selectedPattern: String? = null

    init {
        title = "Select Design Pattern for $structName"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(JLabel("Choose a pattern to apply to struct $structName"), BorderLayout.NORTH)
        panel.add(JScrollPane(patternList), BorderLayout.CENTER)
        return panel
    }

    override fun doOKAction() {
        selectedPattern = patternList.selectedValue.toString()
        super.doOKAction()
    }

    fun getSelectedPattern(): String? = selectedPattern
}
