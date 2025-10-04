package com.novdov.equalityenum

import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyTargetExpression
import com.jetbrains.python.psi.types.PyClassType
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.TypeEvalContext

object EqualityPropertyEnumSupport {
    const val BASE_CLASS_NAME = "EqualityPropertyEnum"
    private const val PROPERTY_PREFIX = "is_"

    fun isEqualityPropertyEnum(type: PyType, context: TypeEvalContext): Boolean {
        if (type !is PyClassType) return false
        return type.pyClass.getAncestorClasses(context).any { ancestorClass ->
            ancestorClass.name == BASE_CLASS_NAME
        }
    }

    fun getPyClassIfEqualityPropertyEnum(type: PyType, context: TypeEvalContext): PyClass? {
        if (type !is PyClassType) return null
        val pyClass = type.pyClass
        return pyClass.takeIf {
            it.getAncestorClasses(context).any { ancestor -> ancestor.name == BASE_CLASS_NAME }
        }
    }

    fun getEnumMembers(pyClass: PyClass): List<PyTargetExpression> {
        return pyClass.classAttributes.filter { attr ->
            attr.name?.all { c -> c.isUpperCase() || c == '_' } == true
        }
    }

    fun toPropertyName(enumMemberName: String): String {
        return "$PROPERTY_PREFIX${enumMemberName.lowercase()}"
    }

    fun isPropertyName(name: String): Boolean {
        return name.startsWith(PROPERTY_PREFIX)
    }
}