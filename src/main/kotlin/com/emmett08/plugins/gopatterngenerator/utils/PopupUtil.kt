package com.emmett08.plugins.gopatterngenerator.utils

import com.emmett08.plugins.gopatterngenerator.template.DesignPattern
import com.goide.psi.*
import com.goide.psi.impl.GoTypeUtil
import com.goide.refactor.GoImplementMethodsHandler
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.codeInsight.generation.ClassMember
import com.intellij.codeInsight.generation.MemberChooserObjectBase
import com.intellij.codeInsight.navigation.BackgroundUpdaterTask
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.ide.util.MemberChooser
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.PopupChooserBuilder
import com.intellij.openapi.util.Iconable
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.RenameableFakePsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.JBList
import com.intellij.util.ObjectUtils
import one.util.streamex.StreamEx
import javax.swing.Icon
import javax.swing.ListSelectionModel

object PopupUtil {

    interface Callback {
        fun onTypeChosen(goTypeSpec: GoTypeSpec)
    }

    interface MultiCallback {
        fun onTypesChosen(list: List<GoTypeSpec>?)
    }

    class GoMemberChooserNode(val element: GoNamedElement) :
        MemberChooserObjectBase(element.qualifiedName ?: "<unnamed>", getPsiElementIcon(element)),
        ClassMember {

        val psiElement: GoNamedElement = element

        override fun getParentNodeDelegate(): ClassMember? {
            return null
        }

        companion object {
            private fun getPsiElementIcon(element: PsiElement): Icon {
                return element.getIcon(Iconable.ICON_FLAG_VISIBILITY) ?: AllIcons.Actions.Stub
            }
        }
    }

    class GoMemberChooser(
        elements: Array<GoMemberChooserNode>,
        project: Project,
        isInsertOverrideVisible: Boolean = false
    ) : MemberChooser<GoMemberChooserNode>(
        elements,
        true,
        true,
        project,
        isInsertOverrideVisible
    ) {
        init {
            this.title = "Choose Fields"
        }
    }

    fun showFieldChooser(spec: GoTypeSpec?, project: Project?, title: String?, isInsertOverrideVisible: Boolean): List<DesignPattern.FieldInfo>? {
        if (spec === null || project == null || spec.getSpecType()?.type !is GoStructType) return null

        val structType = spec.getSpecType()?.type as? GoStructType ?: return null
        val fields = structType.fieldDefinitions.filterNot { it.isBlank }
        val chooserNodes = fields.map { GoMemberChooserNode(it) }.toTypedArray()

        val chooser = GoMemberChooser(chooserNodes, project, isInsertOverrideVisible).apply {
            this.title = title ?: "Select Fields"
        }

        return if (chooser.showAndGet()) {
            chooser.selectedElements?.mapNotNull { node ->
                val element = ObjectUtils.tryCast(node.psiElement, GoNamedElement::class.java)
                element?.let {
                    val goType = PsiTreeUtil.getChildOfType(element, GoType::class.java)
                    val typeName = GoTypeUtil.getReceiverTypeName(goType) ?: "unknown"
                    DesignPattern.FieldInfo(it.toString(), typeName)
                }
            }
        } else {
            null
        }

    }

    fun showMultiStructChooser(
        file: PsiFile, editor: Editor, project: Project, title: String?, callback: MultiCallback
    ) {
        findTypeSpec(editor, file)?.let {
            if (isValidTypeSpec(it)) {
                callback.onTypesChosen(listOf(it))
                return
            }
        }

        val validTypes = StreamEx.of(selectValidTypeSpecs(file))
            .prepend(CreateTypeFakePsiElement(file))
            .toList()

        val navigatableTypes = validTypes.filterIsInstance<NavigatablePsiElement>().toTypedArray()

        PsiElementListNavigator.navigateOrCreatePopup(
            navigatableTypes,
            title ?: "Choose Type",
            null,
            DefaultPsiElementCellRenderer(),
            if (ApplicationManager.getApplication().isUnitTestMode) null else DummyBackgroundUpdaterTask(project)
        ) { selected ->
            callback.onTypesChosen(selected.mapNotNull { it as? GoTypeSpec })  // Safely cast back to GoTypeSpec
        }?.showInBestPositionFor(editor)
    }

    fun showSingleStructChooser(
        file: PsiFile, editor: Editor, project: Project, title: String?, callback: Callback
    ) {
        findTypeSpec(editor, file)?.let {
            if (isValidTypeSpec(it)) {
                callback.onTypeChosen(it)
                return
            }
        }

        val validTypes = selectValidTypeSpecs(file).toMutableList().apply {
            if (!ApplicationManager.getApplication().isUnitTestMode) {
                add(0, CreateTypeFakePsiElement(file))
            }
        }

        PsiElementListNavigator.navigateOrCreatePopup(
            validTypes.toTypedArray(),
            title ?: "Choose Type",
            null,
            DefaultPsiElementCellRenderer(),
            if (ApplicationManager.getApplication().isUnitTestMode) null else DummyBackgroundUpdaterTask(project)
        ) { selected ->
            if (selected.size == 1) callback.onTypeChosen(selected[0] as GoTypeSpec)
        }?.showInBestPositionFor(editor)
    }

    fun showInterfaceChooser(file: PsiFile, project: Project, title: String?, callback: (PsiClass) -> Unit) {
        val interfaces = PsiTreeUtil.collectElementsOfType(file, PsiClass::class.java)
            .filter { it.isInterface }

        if (interfaces.isEmpty()) return

        val list = JBList(interfaces).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        val popup = PopupChooserBuilder(list)
            .setTitle(title ?: "Choose Interface")
            .setMovable(true)
            .setResizable(true)
            .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            .setItemsChosenCallback { selectedValues ->
                (selectedValues.firstOrNull() as? PsiClass)?.let(callback)
            }
            .createPopup()

        val dataContext = DataManager.getInstance().getDataContext(list)
        popup.showInBestPositionFor(dataContext)
    }

    private fun findTypeSpec(editor: Editor, file: PsiFile): GoTypeSpec? {
        val caret = editor.caretModel.primaryCaret
        val offset = caret.offset
        val element = file.findElementAt(offset) ?: file.findElementAt(offset - 1)
        return PsiTreeUtil.getParentOfType(element, GoTypeSpec::class.java)
            ?: PsiTreeUtil.getParentOfType(element, GoTypeDeclaration::class.java)?.typeSpecList?.singleOrNull()
    }

    private fun isValidTypeSpec(typeSpec: GoTypeSpec?) =
        typeSpec !== null && PsiTreeUtil.getParentOfType(typeSpec, GoBlock::class.java) === null &&
                !GoTypeUtil.isInterface(typeSpec)

    fun selectValidTypeSpecs(file: PsiFile): List<NavigatablePsiElement> {
        return PsiTreeUtil.findChildrenOfType(file, GoTypeSpec::class.java)
            .filter(GoImplementMethodsHandler::isValidTypeSpec)
            .filterIsInstance<NavigatablePsiElement>()
    }

    private class CreateTypeFakePsiElement(file: PsiFile) : RenameableFakePsiElement(file) {
        override fun getName(): String = "Create Type..."

        override fun getTypeName(): String = "Type"

        override fun getIcon(): Icon = AllIcons.Actions.IntentionBulb
    }

    private class DummyBackgroundUpdaterTask(project: Project) : BackgroundUpdaterTask(project, "Working...", null) {
        override fun getCaption(size: Int) = null
    }
}
