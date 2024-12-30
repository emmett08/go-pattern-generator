package com.emmett08.plugins.gopatterngenerator.core

import com.intellij.openapi.project.Project

interface PatternSelector {
    fun selectPattern(project: Project): String?
}