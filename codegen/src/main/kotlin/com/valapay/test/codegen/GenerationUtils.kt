package com.valapay.test.codegen

import com.valapay.test.codegen.descriptors.*
import org.reflections.ReflectionUtils
import java.lang.reflect.Method

object GenerationUtils {
    private val LETTERS = "([a-z])([A-Z]+)".toRegex()
    private const val REPLACEMENT = "$1_$2"
    private const val ANNOTATION_TYPE = "annotationType"
    private val STANDARD_METHODS = setOf(
        "hashCode", "toString",
        ANNOTATION_TYPE
    )

    fun createEventBusAddress(router: Router, endPoint: EndPoint): String {
        return "${router.name}.${endPoint.handler}"
    }

    fun createEventBusAddressVarName(router: Router, endPoint: EndPoint): String {
        return "${router.name.toUpperCase()}_${convertToUnderscores(
            endPoint.handler
        )}"
    }

    fun convertToUnderscores(str: String): String {
        return str.replace(
            LETTERS, REPLACEMENT
        ).toUpperCase()
    }

    fun createTsAnnotationsForField(field: Field): String {
        val isArray = field.type.endsWith("[]")
        val isDate = field.type == "Date"
        val fieldType = field.type

        val type = if (isArray) fieldType.substring(0, fieldType.length - 2) else fieldType

        return if (isArray) "\n\t@IsArray()\n\t@ValidateNested()\n\t@Type(() => $type)"
        else if (isDate) "@IsDate()" else ""
    }

    fun createJsAnnotationFromKotlinAnnotation(annotation: Annotation): String {
        val methods = ReflectionUtils.getMethods(annotation.javaClass)
        val name = getJsAnnotationName(annotation)
        val fields = methods
            .filter { !STANDARD_METHODS.contains(it.name) && it.parameterCount == 0 }
        val nameValue = fields.map { getAnnotationValue(it, annotation) }.joinToString(",")
        return "@$name($nameValue)"
    }

    private fun getAnnotationValue(it: Method, annotation: Annotation): String {
        val value = it.invoke(annotation)
        if (value is Array<*>) {
            val stringValues = value.joinToString { value(it) }
            return "[$stringValues]"
        }
        return value(value)
    }

    private fun value(v: Any?): String {
        return if (v is String) "\"$v\"" else v.toString()
    }

    fun getDistinctJSImports(entityFields: List<Field>): List<String> {
        return entityFields
            .flatMap { it.validators }
            .map { getJsAnnotationName(it) }
            .distinct()
    }

    fun getSubEntitiesImports(
        entities: List<Entity>,
        messages: List<Message>,
        entityFields: List<Field>
    ): List<String> {
        val existingMessagesNames = messages.map { it.name }
        val existingEntitiesNames = entities.map { it.name }
        return entityFields
            .map { it.type.replace("[]", "") }
            .filter { existingMessagesNames.contains(it) || existingEntitiesNames.contains(it) }
    }

    private fun getJsAnnotationName(annotation: Annotation): String {
        val methods = ReflectionUtils.getMethods(annotation.javaClass)
        val annotationType = methods.find { it.name == ANNOTATION_TYPE }
        return getSimpleName(annotationType?.invoke(annotation).toString())
    }

    private fun getSimpleName(annoName: String): String {
        val dot = annoName.lastIndexOf('.')
        return annoName.substring(dot + 1)
    }
}
