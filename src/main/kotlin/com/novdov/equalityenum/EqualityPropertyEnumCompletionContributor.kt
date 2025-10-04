package com.novdov.equalityenum

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.icons.PythonPsiApiIcons
import com.jetbrains.python.psi.types.TypeEvalContext

class EqualityPropertyEnumCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            EqualityPropertyEnumCompletionProvider()
        )
    }
}

class EqualityPropertyEnumCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position.parent as? PyQualifiedExpression ?: return
        val qualifier = position.qualifier ?: return

        val typeEvalContext = TypeEvalContext.codeCompletion(
            qualifier.project,
            qualifier.containingFile
        )
        val qualifierType = typeEvalContext.getType(qualifier) ?: return

        val pyClass = EqualityPropertyEnumSupport.getPyClassIfEqualityPropertyEnum(
            qualifierType,
            typeEvalContext
        ) ?: return
        val enumMembers = EqualityPropertyEnumSupport.getEnumMembers(pyClass)

        enumMembers.forEach { member ->
            val memberName = member.name ?: return@forEach
            val propertyName = EqualityPropertyEnumSupport.toPropertyName(memberName)

            val lookupElement = LookupElementBuilder.create(propertyName)
                .withIcon(PythonPsiApiIcons.PropertyGetter)
                .withTypeText(pyClass.name)
                .withTailText(": bool", true)

            result.addElement(lookupElement)
        }
    }
}