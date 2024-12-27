package com.emmett08.plugins.gopatterngenerator.refactoring

import com.goide.GoFileType
import com.intellij.openapi.project.Project
import com.goide.psi.GoFile
import com.intellij.psi.util.PsiTreeUtil
import com.goide.psi.GoTypeSpec
import com.goide.psi.impl.GoElementFactory
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager

class PatternGenerator(private val project: Project, private val psiFile: PsiElement) {

    fun applyPatternToStruct(structName: String, pattern: String) {
        // Validate input
        if (!structName.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))) {
            throw IllegalArgumentException("Invalid Go struct name: $structName")
        }

        // Search for the existing struct in the file
        val struct = PsiTreeUtil.findChildrenOfType(psiFile, GoTypeSpec::class.java)
            .firstOrNull { it.qualifiedName == structName } ?: run {
            NotificationGroupManager.getInstance().getNotificationGroup("Pattern Generator")
                .createNotification(
                    "Struct not found",
                    "Struct with name '$structName' was not found in the file.",
                    NotificationType.ERROR
                )
                .notify(project)
            return
        }

        // Generate the appropriate code based on the pattern
        val generatedCode = when (pattern) {
            "Singleton" -> generateSingleton(structName)
            "Factory" -> generateFactory(structName)
            else -> {
                NotificationGroupManager.getInstance().getNotificationGroup("Pattern Generator")
                    .createNotification(
                        "Unsupported pattern",
                        "The pattern '$pattern' is not supported.",
                        NotificationType.ERROR
                    )
                    .notify(project)
                return
            }
        }

        // Create a temporary Go file to parse the generated code
        val tempFile = PsiFileFactory.getInstance(project)
            .createFileFromText("generated.go", GoFileType.INSTANCE, generatedCode) as? PsiElement
            ?: throw IllegalStateException("Failed to create temporary file for generated code.")

        // Extract the first GoTypeSpec element from the temporary file
        val newElement = PsiTreeUtil.findChildrenOfType(tempFile, GoTypeSpec::class.java)
            .firstOrNull() ?: throw IllegalArgumentException("Generated code does not include a valid Go structure.")

        WriteCommandAction.runWriteCommandAction(project) {
            // Check if an element with the same name already exists
            if (PsiTreeUtil.findChildrenOfType(psiFile, GoTypeSpec::class.java)
                    .any { it.qualifiedName == newElement.qualifiedName }) {
                throw IllegalArgumentException("An element with the name '${newElement.qualifiedName}' already exists.")
            }

            // Add the new element to the original file
            val addedElement = psiFile.add(newElement)

            // Reformat the added element to adhere to the file's code style
            CodeStyleManager.getInstance(project).reformat(addedElement)
        }
    }

    private fun generateSingleton(name: String) = """
        var instance *$name

        func GetInstance() *$name {
            if instance == nil {
                instance = &$name{}
            }
            return instance
        }
    """.trimIndent()

    private fun generateFactory(name: String) = """
        type ${name}Factory struct {}

        func (f ${name}Factory) Create() $name {
            return $name{}
        }
    """.trimIndent()
}
