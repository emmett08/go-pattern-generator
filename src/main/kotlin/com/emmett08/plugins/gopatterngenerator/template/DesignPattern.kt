package com.emmett08.plugins.gopatterngenerator.template

import com.goide.GoTypes
import com.goide.psi.GoFile
import com.goide.psi.GoTypeSpec
import com.goide.psi.impl.GoPsiImplUtil
import com.goide.psi.impl.GoTypeUtil
import com.goide.refactor.template.GoTemplate
import com.goide.refactor.util.GoRefactoringUtil
import com.intellij.codeInsight.actions.OptimizeImportsAction
import com.intellij.codeInsight.actions.ReformatCodeAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.*

import com.intellij.openapi.editor.EditorFactory

abstract class DesignPattern(protected val event: AnActionEvent) {
    protected val file: GoFile? = event.getData(LangDataKeys.PSI_FILE) as GoFile?
    protected val project: Project? = event.project
    protected val editor: Editor? = event.getData(LangDataKeys.EDITOR) ?: EditorFactory.getInstance().allEditors.firstOrNull()

    abstract fun createTemplate(template: GoTemplate)
    abstract val goTypeSpec: GoTypeSpec?

    private fun collectUniqueImports(file: GoFile, editor: Editor): Set<GoRefactoringUtil.Import> {
        return file.types
            .filterNot { GoTypeUtil.isInterface(it) }
            .flatMap { getImports(file, editor, it) }
            .toSet() // Keeps the set immutable unless mutation is necessary later.
    }

    fun generateText(file: GoFile?, editor: Editor?, goTypeSpec: GoTypeSpec?) {
        requireNotNull(file) { "GoFile cannot be null." }
        requireNotNull(editor) { "Editor cannot be null." }
        requireNotNull(goTypeSpec) { "GoTypeSpec cannot be null." }

        val template = GoTemplate(file).apply { createTemplate(this) }
        val uniqueImports = collectUniqueImports(file, editor)

        /*
        Cannot access class 'com. intellij. openapi. editor. Editor'. Check your module classpath for missing or conflicting dependencies
Cannot access class 'com. intellij. openapi. util. TextRange'. Check your module classpath for missing or conflicting dependencies
         */
        template.startTemplate(
            editor,
            calculateOffset(editor, goTypeSpec),
            "Generate ${this::class.java.simpleName}",
            null,
            uniqueImports
        )

        reformatCode()
        optimizeImports()
    }

    protected fun checkDuplicateMethod(method: String, receiver: String?): Boolean {
        return file?.methods?.any { it.name == method && receiver == it.receiverType?.text } ?: false
    }

    protected fun checkDuplicateStructOrInterface(name: String): Boolean {
        return file?.types?.any { it.name == name } ?: false
    }

    private fun calculateOffset(editor: Editor, typeSpec: GoTypeSpec?): Int {
        val typeDeclaration = PsiTreeUtil.getParentOfType(typeSpec, GoTypeDeclaration::class.java)
        return typeDeclaration?.let {
            PsiTreeUtil.nextVisibleLeaf(it)?.takeIf { leaf ->
                leaf is LeafPsiElement && leaf.elementType == GoTypes.SEMICOLON
            }?.textRange?.endOffset ?: it.textRange.endOffset
        } ?: editor.caretModel.offset
    }

    private fun optimizeImports() {
        OptimizeImportsAction.actionPerformedImpl(event.dataContext)
    }

    private fun reformatCode() {
        ReformatCodeAction().actionPerformed(event)
    }

    protected fun templateNewLine(template: GoTemplate, num: Int) {
        repeat(num) { template.addTextSegment("\n") }
    }

    protected fun templateAddIndentation(template: GoTemplate, num: Int) {
        repeat(num) { template.addTextSegment("\t") }
    }

    data class FieldInfo(val name: String, val type: String)

    data class MethodInfo(
        var name: String = "",
        var result: String = "",
        var parameters: List<FieldInfo> = emptyList(),
        var results: List<FieldInfo> = emptyList()
    ) {
        companion object {
            fun parse(interfaceToImpl: GoTypeSpec): List<MethodInfo> {
                if (!interfaceToImpl.isValid) return emptyList()
                return interfaceToImpl.allMethods.map { method ->
                    MethodInfo(
                        name = method.name ?: "",
                        parameters = method.signature?.parameters?.definitionList?.map {
                            FieldInfo(it.name ?: "", it.goType(null)?.text ?: "")
                        } ?: emptyList(),
                        results = method.signature?.result?.parameters?.definitionList?.map {
                            FieldInfo(it.name ?: "", it.goType(null)?.text ?: "")
                        } ?: emptyList(),
                        result = method.signature?.resultType?.text ?: ""
                    )
                }
            }
        }
    }

    companion object {
        fun getImports(file: GoFile, editor: Editor, typeSpecToGenerate: GoTypeSpec?): Set<GoRefactoringUtil.Import> {
            val importsToAdd = mutableSetOf<GoRefactoringUtil.Import>()
            typeSpecToGenerate?.allMethods?.forEach { method ->
                if (!isAlreadyImplemented(method, typeSpecToGenerate.allMethods)) {
                    method.accept(object : PsiRecursiveElementWalkingVisitor() {
                        override fun visitElement(o: PsiElement) {
                            if (o is GoTypeReferenceExpression) {
                                val resolveType = o.resolveType(GoPsiImplUtil.createContextOnElement(file))
                                importsToAdd.addAll(
                                    GoRefactoringUtil.getTypeTextWithImports(file, resolveType, false).imports
                                )
                            } else super.visitElement(o)
                        }
                    })
                }
            }
            return importsToAdd
        }

        private fun isAlreadyImplemented(
            method: GoNamedSignatureOwner,
            existingMethods: List<GoNamedSignatureOwner>
        ): Boolean {
            return existingMethods.any {
                it.name == method.name && GoTypeUtil.areSignaturesIdentical(
                    method.signature,
                    it.signature,
                    true,
                    true,
                    null
                    
                )
            }
                    
            }
        }
    }
}
