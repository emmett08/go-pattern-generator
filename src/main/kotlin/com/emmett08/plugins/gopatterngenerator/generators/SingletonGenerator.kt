package com.emmett08.plugins.gopatterngenerator.generators

class SingletonGenerator : PatternGenerator {
    override fun generate(pattern: String, attributes: List<String>) = """
        package main

        var instance *${pattern}

        type $pattern struct{}

        func GetInstance() *${pattern} {
            if instance == nil {
                instance = &${pattern}{}
            }
            return instance
        }
    """.trimIndent()
}
