package com.valapay.test.codegen.descriptors


import com.valapay.test.codegen.typemappers.TypeMapper
import com.valapay.test.annotations.FindBy
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.lang.reflect.Type


class MessagesCreator(private val typeMapper: TypeMapper, private val frontendAnnotationsPackageName: String) {


    fun createMessages(packageName: String): List<Message> {
        val reflections = Reflections(packageName, SubTypesScanner(false))
        return reflections.allTypes.map { Class.forName(it) }.map { createMessages(it) }
    }


    private fun createMessages(aClass: Class<*>): Message {
        return Message(aClass.simpleName, createFields(aClass))
    }

    fun createFields(aClass: Class<*>): List<Field> {
        return ReflectionUtils.getAllFields(aClass).map { createField(it) }
    }

    private fun createField(field: java.lang.reflect.Field): Field {
        val annotations = field.declaredAnnotations
        return Field(
            field.name, typeMapper.map(field.type),
            createConstraints(annotations),
            annotations.map { anno -> anno::annotationClass.get() }.contains(FindBy::class)
        )
    }

    private fun createConstraints(annotations: Array<out Annotation>): List<Annotation> {
        return annotations.filter { it.toString().startsWith("@$frontendAnnotationsPackageName") }
    }
}