package com.emmett08.plugins.gopatterngenerator.generators

class SingletonGenerator : PatternGenerator {
    override fun generate() = """
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