package com.emmett08.plugins.gopatterngenerator.ui

import com.emmett08.plugins.gopatterngenerator.core.PatternWizard
import com.emmett08.plugins.gopatterngenerator.generators.SingletonGenerator
import com.emmett08.plugins.gopatterngenerator.handlers.FileHandler
import com.intellij.openapi.project.Project

class SingletonWizard : PatternWizard {
    override fun generate(project: Project, pattern: String) {
        val code = SingletonGenerator().generate(pattern, emptyList())
        FileHandler().overwriteSelectedCode(code, project)
    }
}
