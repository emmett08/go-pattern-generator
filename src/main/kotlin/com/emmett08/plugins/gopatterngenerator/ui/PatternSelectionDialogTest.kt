package com.emmett08.plugins.gopatterngenerator.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.junit.jupiter.api.*
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic

class PatternSelectionDialogTest {

    private lateinit var patternSelectionDialog: PatternSelectionDialog
    private var project: Project? = null

    @BeforeEach
    fun setUp() {
        patternSelectionDialog = PatternSelectionDialog()
        project = mock()
    }

    @AfterEach
    fun tearDown() {
        project = null
    }

    @Test
    fun `show should return selected pattern when a valid option is chosen`() {
        mockStatic(Messages::class.java).use { mockedMessages: MockedStatic<Messages> ->
        whenever(
                Messages.showDialog(
                    project,
                    "Choose a design pattern to generate:",
                    "Generate Go Pattern",
                    arrayOf("Singleton", "Factory", "Builder"),
                    0,
                    Messages.getQuestionIcon()
                )
            ).thenReturn(1) // User selects "Factory"

            val result = patternSelectionDialog.show(project!!)

            assertEquals("Factory", result)
        }
    }

    @Test
    fun `show should return null when the user cancels the dialog`() {
        mockStatic(Messages::class.java).use { mockedMessages: MockedStatic<Messages> ->
        whenever(
                Messages.showDialog(
                    project,
                    "Choose a design pattern to generate:",
                    "Generate Go Pattern",
                    arrayOf("Singleton", "Factory", "Builder"),
                    0,
                    Messages.getQuestionIcon()
                )
            ).thenReturn(-1) // User cancels the dialog

            val result = patternSelectionDialog.show(project!!)

            assertNull(result)
        }
    }

    @Test
    fun `show should return the default option when no specific choice is made`() {
        mockStatic(Messages::class.java).use { mockedMessages: MockedStatic<Messages> ->
        whenever(
                Messages.showDialog(
                    project,
                    "Choose a design pattern to generate:",
                    "Generate Go Pattern",
                    arrayOf("Singleton", "Factory", "Builder"),
                    0,
                    Messages.getQuestionIcon()
                )
            ).thenReturn(0) // User selects "Singleton" (default)

            val result = patternSelectionDialog.show(project!!)

            assertEquals("Singleton", result)
        }
    }
}
