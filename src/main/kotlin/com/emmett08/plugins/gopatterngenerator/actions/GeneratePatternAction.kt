package com.emmett08.plugins.gopatterngenerator.actions

import com.emmett08.plugins.gopatterngenerator.core.PatternGenerationHandler
import com.emmett08.plugins.gopatterngenerator.core.PatternWizardFactory
import com.emmett08.plugins.gopatterngenerator.ui.DropdownPatternSelector
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GeneratePatternAction : AnAction() {
    private val generatorFactory = PatternWizardFactory()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val patternSelector = DropdownPatternSelector(project)
        val patternHandler = PatternGenerationHandler(patternSelector, generatorFactory)
        patternHandler.handle(project)

        // val baseDir = project.guessProjectDir()
    }
}
