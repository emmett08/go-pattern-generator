package com.emmett08.plugins.gopatterngenerator.core

import com.intellij.openapi.project.Project

interface PatternWizard {
    fun generate(project: Project, pattern: String)
}