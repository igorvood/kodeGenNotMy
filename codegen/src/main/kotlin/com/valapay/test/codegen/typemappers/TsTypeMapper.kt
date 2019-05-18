package com.valapay.test.codegen.typemappers

object TsTypeMapper : TypeMapper {
    override fun map( type: Class<*>): String {
        if (type.isEnum){
            return "string"
        }
        val name = type.simpleName
        return when (name) {
            "Integer" -> "number"
            "int" -> "number"
            "long" -> "number"
            "String" -> "string"
            "Boolean" -> "boolean"
            "Long" -> "number"
            "BigDecimal" -> "number"
            "Map" -> "{ [key: string]: any }"
            else -> name
        }
    }
}