package com.emmett08.plugins.gopatterngenerator.generators

interface PatternGenerator {
    fun generate(pattern: String, attributes: List<String>): String
}
