package com.emmett08.plugins.gopatterngenerator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.Messages
import java.io.IOException

class GeneratePatternAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val options = arrayOf("Singleton", "Factory", "Builder")
        val selection = Messages.showDialog(
            project,
            "Choose a design pattern to generate:",
            "Generate Go Pattern",
            options,
            0,
            Messages.getQuestionIcon()
        )

        if (selection == -1) return
        val patternCode = when (options[selection]) {
            "Singleton" -> getSingleton()
            "Factory" -> getFactory()
            "Builder" -> getBuilder()
            else -> ""
        }

        val baseDir = project.guessProjectDir()
        WriteAction.run<IOException> {
            try {
                val file = baseDir?.createChildData(this, "pattern.go")
                file?.setBinaryContent(patternCode.toByteArray())
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Failed to create file", "Error")
            }
        }
    }

    private fun getSingleton(): String {
        return """
            package main

            var instance *Singleton

            type Singleton struct{}

            func GetInstance() *Singleton {
                if instance == nil {
                    instance = &Singleton{}
                }
                return instance
            }
        """.trimIndent()
    }

    private fun getFactory(): String {
        return """
            package main

            type Shape interface {
                Draw()
            }

            type Circle struct{}

            func (c Circle) Draw() {
                println("Circle Draw")
            }

            type ShapeFactory struct{}

            func (s ShapeFactory) CreateShape(shapeType string) Shape {
                switch shapeType {
                case "circle":
                    return Circle{}
                default:
                    return nil
                }
            }
        """.trimIndent()
    }

    private fun getBuilder(): String {
        return """
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
}
