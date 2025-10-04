package com.novdov.equalityenum

import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.PyQualifiedExpression
import com.jetbrains.python.psi.impl.PyBuiltinCache
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.PyTypeProviderBase
import com.jetbrains.python.psi.types.TypeEvalContext

class EqualityPropertyEnumTypeProvider : PyTypeProviderBase() {
    override fun getReferenceType(
        referenceTarget: PsiElement,
        context: TypeEvalContext,
        anchor: PsiElement?
    ): Ref<PyType>? {
        if (anchor !is PyQualifiedExpression) return null

        val referencedName = anchor.referencedName ?: return null
        if (!EqualityPropertyEnumSupport.isPropertyName(referencedName)) return null

        val qualifier = anchor.qualifier ?: return null
        val qualifierType = context.getType(qualifier) ?: return null

        if (!EqualityPropertyEnumSupport.isEqualityPropertyEnum(qualifierType, context)) return null

        val boolType = PyBuiltinCache.getInstance(anchor).boolType ?: return null
        return Ref.create(boolType)
    }
}