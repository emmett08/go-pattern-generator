package com.emmett08.plugins.gopatterngenerator.generators

class BuilderGenerator : PatternGenerator {
    override fun generate(pattern: String, attributes: List<String>) = """
        package main

        type $pattern struct {
            ${attributes.joinToString("\n") { attr -> "$attr string" }}
        }

        type ${pattern}Builder struct {
            component $pattern
        }

        ${attributes.joinToString("\n") { attr ->
        """
            func (cb *${pattern}Builder) Set${attr.replaceFirstChar { it.uppercase() }}(v string) *${pattern}Builder {
                cb.component.${attr} = v
                return cb
            }
            """.trimIndent()
    }}

        func (cb *${pattern}Builder) Build() $pattern {
            return cb.component
        }
    """.trimIndent()
}
