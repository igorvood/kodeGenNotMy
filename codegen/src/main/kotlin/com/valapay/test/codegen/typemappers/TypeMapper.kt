package com.valapay.test.codegen.typemappers

interface TypeMapper {
    fun map(type: Class<*>): String
}