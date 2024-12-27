package com.emmett08.plugins.gopatterngenerator.template.pattern

import com.goide.psi.GoTypeSpec
import com.goide.refactor.template.GoTemplate
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.openapi.actionSystem.AnActionEvent
import com.emmett08.plugins.gopatterngenerator.template.DesignPattern

class SingletonTemplate(event: AnActionEvent, private val typeSpec: GoTypeSpec, private val isSafe: Boolean) : DesignPattern(event) {
    private val INSTANCE_VAR = "INSTANCE_VAR"

    override fun createTemplate(template: GoTemplate) {
        if (isSafe) createSafeSingleton(template) else createUnSafeSingleton(template)
    }

    override fun getGoTypeSpec(): GoTypeSpec = typeSpec

    private fun createUnSafeSingleton(template: GoTemplate): GoTemplate {
        val typeName = typeSpec.name
        template.addTextSegment("var ")
        template.addPrimaryVariable(INSTANCE_VAR, ConstantNode("instance"))
        template.addTextSegment("*$typeName\n\n")
        template.addTextSegment("func ")
        template.addVariable("GetInstanceUnsafe", true)
        template.addTextSegment("() *$typeName {\n")
        template.addTextSegment("\tif ")
        template.addVariableSegment(INSTANCE_VAR)
        template.addTextSegment(" ==nil {\n\t\t")
        template.addVariableSegment(INSTANCE_VAR)
        template.addTextSegment(" =&$typeName{}\n\t}\n\treturn ")
        template.addVariableSegment(INSTANCE_VAR)
        template.addEndVariable()
        template.addTextSegment("\n}")
        return template
    }

    private fun createSafeSingleton(template: GoTemplate): GoTemplate {
        val typeName = typeSpec.name
        template.addTextSegment("var ")
        template.addPrimaryVariable(INSTANCE_VAR, ConstantNode("instance"))
        template.addTextSegment("*$typeName\n\n")
        template.addTextSegment("var ")
        template.addVariableSegment(INSTANCE_VAR)
        template.addTextSegment("Once sync.Once\n\n")
        template.addTextSegment("func ")
        template.addVariable("GetInstanceSafe", true)
        template.addTextSegment("() *$typeName {\n\t")
        template.addVariableSegment(INSTANCE_VAR)
        template.addTextSegment("Once.Do(func() {\n\t\t")
        template.addVariableSegment(INSTANCE_VAR)
        template.addTextSegment(" = &$typeName{}\n\t})\n\treturn ")
        template.addVariableSegment(INSTANCE_VAR)
        template.addEndVariable()
        template.addTextSegment("\n}")
        return template
    }
}
