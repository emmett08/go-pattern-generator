package com.emmett08.plugins.gopatterngenerator.template.pattern

import com.goide.psi.GoTypeSpec
import com.goide.refactor.template.GoTemplate
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.text.StringUtil
import com.emmett08.plugins.gopatterngenerator.template.DesignPattern
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil

class BuilderTemplate(event: AnActionEvent, private val goTypeSpec: GoTypeSpec) : DesignPattern(event) {

    override fun createTemplate(template: GoTemplate) {
        val fields = PopupUtil.getChooseFieldPopup(goTypeSpec, project, null) ?: return
        if (fields.isEmpty()) return

        val typeName = goTypeSpec.name ?: return
        createSetterAndGetter(template, typeName, fields)
        createBuildInterface(template, typeName, fields)
        createBuilderStruct(template, typeName)
        createInterfaceImpl(template, typeName, fields)
    }

    override fun getGoTypeSpec(): GoTypeSpec = goTypeSpec

    private fun createInterfaceImpl(template: GoTemplate, typeName: String, fields: List<FieldInfo>) {
        val builderName = StringUtil.capitalize(typeName) + "Builder"
        val lowerName = StringUtil.decapitalize(typeName)

        val builder = buildString {
            appendLine("func New${builderName}($lowerName *$typeName) *$builderName {")
            appendLine("\treturn &$builderName{ $lowerName: $lowerName }")
            appendLine("}")

            appendLine("func (builder $builderName) Create${setupFunctionParameters(fields)} *$typeName {")
            append("\treturn builder")
            fields.forEach {
                append(".Set${StringUtil.capitalize(it.name)}(${StringUtil.decapitalize(it.name)})")
            }
            appendLine(".Build()")
            appendLine("}")

            appendLine("func (builder $builderName) Build() *$typeName {")
            appendLine("\treturn builder.$lowerName")
            appendLine("}")

            fields.forEach {
                val fieldCap = StringUtil.capitalize(it.name)
                val fieldDecap = StringUtil.decapitalize(it.name)
                appendLine("func (builder $builderName) Set$fieldCap($fieldDecap ${it.type}) Builder {")
                appendLine("\tif builder.$lowerName == nil {")
                appendLine("\t\tbuilder.$lowerName = &$typeName{}")
                appendLine("\t}")
                appendLine("\tbuilder.$lowerName.Set$fieldCap($fieldDecap)")
                appendLine("\treturn builder")
                appendLine("}")
            }
        }

        template.addTextSegment(builder)
    }

    private fun createBuilderStruct(template: GoTemplate, typeName: String) {
        val builderStruct = """
            |type ${StringUtil.capitalize(typeName)}Builder struct {
            |   ${StringUtil.decapitalize(typeName)} *$typeName
            |}
        """.trimMargin()
        template.addTextSegment(builderStruct)
    }

    private fun createBuildInterface(template: GoTemplate, typeName: String, fields: List<FieldInfo>) {
        val interfaceText = buildString {
            appendLine("type Builder interface {")
            fields.forEach {
                appendLine("\tSet${StringUtil.capitalize(it.name)}(${it.name} ${it.type}) Builder")
            }
            appendLine("\tBuild() *$typeName")
            appendLine("\tCreate${setupFunctionParameters(fields)} *$typeName")
            appendLine("}")
        }
        template.addTextSegment(interfaceText)
    }

    private fun createSetterAndGetter(template: GoTemplate, typeName: String, fields: List<FieldInfo>) {
        val firstLower = StringUtil.decapitalize(typeName)
        val setterGetter = buildString {
            fields.forEach { field ->
                val fieldCap = StringUtil.capitalize(field.name)
                appendLine("func ($firstLower *$typeName) Set$fieldCap(${field.name} ${field.type}) {")
                appendLine("\t$firstLower.${field.name} = ${field.name}")
                appendLine("}")
                appendLine("func ($firstLower *$typeName) Get$fieldCap() ${field.type} {")
                appendLine("\treturn $firstLower.${field.name}")
                appendLine("}")
            }
        }
        template.addTextSegment(setterGetter)
    }
}
