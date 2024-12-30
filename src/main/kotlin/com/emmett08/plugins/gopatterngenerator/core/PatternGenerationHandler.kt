package com.emmett08.plugins.gopatterngenerator.core

import com.intellij.openapi.project.Project

class PatternGenerationHandler(
    private val selector: PatternSelector,
    private val factory: PatternWizardFactory
) {
    fun handle(project: Project) {
        val pattern = selector.selectPattern(project) ?: return
        val wizard = factory.getWizard(pattern)
        wizard.generate(project, pattern)
    }
}