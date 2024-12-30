package com.emmett08.plugins.gopatterngenerator.generators

class FactoryGenerator : PatternGenerator {
    override fun generate(pattern: String, attributes: List<String>) = """
        package main

        type $pattern interface {
            Create()
        }

        type Concrete${pattern} struct{}

        func (c Concrete${pattern}) Create() {
            println("$pattern created")
        }

        type ${pattern}Factory struct{}

        func (f ${pattern}Factory) Create${pattern}() $pattern {
            return Concrete${pattern}{}
        }
    """.trimIndent()
}
