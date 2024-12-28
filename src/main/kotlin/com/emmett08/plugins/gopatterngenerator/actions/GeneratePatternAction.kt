package com.emmett08.plugins.gopatterngenerator.actions

import com.emmett08.plugins.gopatterngenerator.generators.PatternGeneratorFactory
import com.emmett08.plugins.gopatterngenerator.handlers.FileHandler
import com.emmett08.plugins.gopatterngenerator.ui.PatternSelectionDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.guessProjectDir

class GeneratePatternAction : AnAction() {
    private val generatorFactory = PatternGeneratorFactory()
    private val fileHandler = FileHandler()
    private val dialog = PatternSelectionDialog()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedPattern = dialog.show(project) ?: return
        val generator = generatorFactory.createGenerator(selectedPattern)
        val patternCode = generator.generate()

        val baseDir = project.guessProjectDir()
        fileHandler.writeToFile(baseDir, patternCode, project)
    }
}
