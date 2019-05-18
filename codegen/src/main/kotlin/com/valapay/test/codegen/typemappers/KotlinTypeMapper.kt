package com.valapay.test.codegen.typemappers

object KotlinTypeMapper : TypeMapper {
    override fun map(type: Class<*>): String {
        val name = type.simpleName
        return when (name) {
            "Integer" -> "Int"
            "int" -> "Int"
            "long" -> "Long"
            else -> name
        }
    }
}