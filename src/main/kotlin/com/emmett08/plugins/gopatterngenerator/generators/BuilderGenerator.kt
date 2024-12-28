package com.emmett08.plugins.gopatterngenerator.generators

class BuilderGenerator : PatternGenerator {
    override fun generate() = """
        package main

        type Builder struct {
            part1 string
            part2 string
        }

        func (b *Builder) SetPart1(p string) *Builder {
            b.part1 = p
            return b
        }

        func (b *Builder) SetPart2(p string) *Builder {
            b.part2 = p
            return b
        }

        func (b *Builder) Build() Product {
            return Product{b.part1, b.part2}
        }

        type Product struct {
            part1 string
            part2 string
        }
    """.trimIndent()
}