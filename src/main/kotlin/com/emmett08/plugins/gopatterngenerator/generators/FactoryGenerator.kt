package com.emmett08.plugins.gopatterngenerator.generators

class FactoryGenerator : PatternGenerator {
    override fun generate() = """
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