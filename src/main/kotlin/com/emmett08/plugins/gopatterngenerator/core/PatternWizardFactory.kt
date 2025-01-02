package com.emmett08.plugins.gopatterngenerator.core

import com.emmett08.plugins.gopatterngenerator.ui.BuilderWizard
import com.emmett08.plugins.gopatterngenerator.ui.FactoryWizard
import com.emmett08.plugins.gopatterngenerator.ui.SingletonWizard

class PatternWizardFactory {
    fun getWizard(pattern: String): PatternWizard =
        when (pattern) {
            "Singleton" -> SingletonWizard()
            "Factory" -> FactoryWizard()
            "Builder" -> BuilderWizard()
            else -> throw IllegalArgumentException("Unknown pattern: $pattern")
        }
}
