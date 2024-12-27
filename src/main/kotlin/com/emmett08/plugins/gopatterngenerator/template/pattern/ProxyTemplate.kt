package com.emmett08.plugins.gopatterngenerator.template.pattern

import com.goide.psi.GoTypeSpec
import com.goide.refactor.template.GoTemplate
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.text.StringUtil
import com.emmett08.plugins.gopatterngenerator.template.DesignPattern
import com.emmett08.plugins.gopatterngenerator.utils.PopupUtil

class ProxyTemplate(event: AnActionEvent, private val structType: GoTypeSpec, private val interfaceType: GoTypeSpec) : DesignPattern(event) {

    override fun createTemplate(template: GoTemplate) {
        val structName = structType.name
        val interName = interfaceType.name
        PopupUtil.createUnImplementMethod(file, editor, structType, interfaceType)
        createProxyStruct(template, structName, interName)
        createProxyMethod(template, structType, interfaceType)
    }

    private fun createProxyMethod(template: GoTemplate, structType: GoTypeSpec, interfaceType: GoTypeSpec) {
        val proxyerName = "Proxy" + StringUtil.capitalize(structType.name)
        val builder = buildString {
            appendLine("func (p $proxyerName) ProxyFor(${StringUtil.decapitalize(interfaceType.name)} *${interfaceType.name}) {")
            appendLine("\tp.${StringUtil.decapitalize(interfaceType.name)} = *${StringUtil.decapitalize(interfaceType.name)}")
            appendLine("}")

            MethodInfo.Parse(interfaceType).forEach {
                appendLine("func (p $proxyerName) ${it.name}${it.parametersText} ${it.resultsText} {")
                appendLine("\t/*do something before*/")
                appendLine("\tp.${StringUtil.decapitalize(interfaceType.name)}.${it.name}${it.parametersTextNoType}")
                appendLine("\t/*do something after*/")
                appendLine("}")
            }
        }
        template.addTextSegment(builder)
    }

    private fun createProxyStruct(template: GoTemplate, structName: String, interName: String) {
        val proxyStruct = """
            |type Proxy${StringUtil.capitalize(structName)} struct {
            |   ${StringUtil.decapitalize(interName)} $interName
            |}
        """.trimMargin()
        template.addTextSegment(proxyStruct)
    }

    override fun getGoTypeSpec(): GoTypeSpec = structType
}
