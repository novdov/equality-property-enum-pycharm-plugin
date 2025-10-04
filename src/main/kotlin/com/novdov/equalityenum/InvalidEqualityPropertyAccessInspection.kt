package com.novdov.equalityenum

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.types.TypeEvalContext

class InvalidEqualityPropertyAccessInspection : PyInspection() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : PyInspectionVisitor(
            holder,
            TypeEvalContext.codeAnalysis(session.file.project, session.file)
        ) {
            override fun visitPyReferenceExpression(node: PyReferenceExpression) {
                val referencedName = node.name ?: return
                if (!EqualityPropertyEnumSupport.isPropertyName(referencedName)) return

                val qualifier = node.qualifier ?: return
                val qualifierType = myTypeEvalContext.getType(qualifier) ?: return

                val pyClass = EqualityPropertyEnumSupport.getPyClassIfEqualityPropertyEnum(
                    qualifierType,
                    myTypeEvalContext
                ) ?: return
                val enumMembers = EqualityPropertyEnumSupport.getEnumMembers(pyClass)
                val validPropertyNames = enumMembers.mapNotNull { it.name }
                    .map { EqualityPropertyEnumSupport.toPropertyName(it) }

                if (referencedName !in validPropertyNames) {
                    registerProblem(
                        node,
                        "${EqualityPropertyEnumSupport.BASE_CLASS_NAME} member '${pyClass.name}' has no property '$referencedName'. Available: ${
                            validPropertyNames.joinToString(", ")
                        }"
                    )
                }
            }
        }
    }
}