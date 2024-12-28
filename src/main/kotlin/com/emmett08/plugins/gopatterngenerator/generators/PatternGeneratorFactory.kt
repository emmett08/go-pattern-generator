package com.emmett08.plugins.gopatterngenerator.generators

class PatternGeneratorFactory {
    fun createGenerator(pattern: String): PatternGenerator {
        return when (pattern) {
            "Singleton" -> SingletonGenerator()
            "Factory" -> FactoryGenerator()
            "Builder" -> BuilderGenerator()
            else -> throw IllegalArgumentException("Unknown pattern: $pattern")
        }
    }
}