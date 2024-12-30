package com.emmett08.plugins.gopatterngenerator.ui

import com.emmett08.plugins.gopatterngenerator.core.PatternWizard
import com.intellij.openapi.project.Project
import com.emmett08.plugins.gopatterngenerator.handlers.FileHandler
import com.emmett08.plugins.gopatterngenerator.generators.BuilderGenerator
import com.emmett08.plugins.gopatterngenerator.utils.EditorUtils

class BuilderWizard : PatternWizard {
    override fun generate(project: Project, pattern: String) {
        val selectedText = EditorUtils.getSelectedText(project)
        val generator = BuilderGenerator()
        val builderCode = generator.generate(pattern, selectedText)

        val fileHandler = FileHandler()
        fileHandler.overwriteSelectedCode(builderCode, project)
    }
}
