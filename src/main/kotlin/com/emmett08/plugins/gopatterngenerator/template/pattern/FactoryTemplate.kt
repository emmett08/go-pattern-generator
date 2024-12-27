package com.emmett08.plugins.gopatterngenerator.template.pattern

import com.goide.psi.GoTypeSpec
import com.goide.refactor.template.GoTemplate
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.text.StringUtil
import com.emmett08.plugins.gopatterngenerator.template.DesignPattern
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil

class FactoryTemplate(event: AnActionEvent, private val lists: List<GoTypeSpec>, private val interfaceToImpl: GoTypeSpec) : DesignPattern(event) {

    override fun createTemplate(template: GoTemplate) {
        lists.forEach { structType ->
            PopupUtil.createUnImplementMethod(file, editor, structType, interfaceToImpl)
        }
        createFactoryStruct(template)
    }

    private fun createFactoryStruct(template: GoTemplate) {
        val interfaceName = StringUtil.capitalize(interfaceToImpl.name)
        template.addTextSegment("\n\ntype ${interfaceName}Factory struct{}\n")

        val builder = buildString {
            appendLine("func (${StringUtil.decapitalize(interfaceName)}Factory *${interfaceName}Factory) get$interfaceName(name string) ${interfaceToImpl.name} {")
            appendLine("\tif name == \"\" {")
            appendLine("\t\treturn nil")
            appendLine("\t}")
            appendLine("\tswitch strings.ToLower(name) {")

            lists.forEach {
                appendLine("\t\tcase \"${StringUtil.toLowerCase(it.name)}\":")
                appendLine("\t\t\treturn new(${it.name})")
            }

            appendLine("\t\tdefault:")
            appendLine("\t\t\treturn nil")
            appendLine("\t}")
            appendLine("}")
        }
        template.addTextSegment(builder)
    }

    override fun getGoTypeSpec(): GoTypeSpec = interfaceToImpl
}
