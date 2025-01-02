package com.emmett08.plugins.gopatterngenerator.ui

import com.emmett08.plugins.gopatterngenerator.core.PatternWizard
import com.emmett08.plugins.gopatterngenerator.generators.FactoryGenerator
import com.emmett08.plugins.gopatterngenerator.handlers.FileHandler
import com.intellij.openapi.project.Project

class FactoryWizard : PatternWizard {
    override fun generate(project: Project, pattern: String) {
        val code = FactoryGenerator().generate(pattern, emptyList())
        FileHandler().overwriteSelectedCode(code, project)
    }
}
