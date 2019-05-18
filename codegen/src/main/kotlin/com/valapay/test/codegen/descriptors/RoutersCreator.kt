package com.valapay.test.codegen.descriptors

import com.valapay.test.codegen.typemappers.TypeMapper
import com.valapay.test.annotations.AsyncHandler
import com.valapay.test.annotations.Endpoint
import com.valapay.test.annotations.EndpointController
import com.valapay.test.annotations.HttpMethodName

import org.apache.commons.lang3.StringUtils
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.lang.reflect.Method


class RoutersCreator(private val typeMapper: TypeMapper, private val endpointsPackage:String ) {

    fun createRouters(): List<Router> {
        val reflections = Reflections(endpointsPackage, SubTypesScanner(false))
        return reflections.allTypes.map {
            createRouter(
                Class.forName(
                    it
                )
            )
        }
    }

    private fun createRouter(aClass: Class<*>): Router {
        return Router(aClass.simpleName, getUrl(aClass),
            ReflectionUtils.getAllMethods(aClass).map {
                createEndpoint(it)
            })
    }

    private fun getUrl(aClass: Class<*>): String {
        return aClass.getAnnotation(EndpointController::class.java).url
    }

    private fun getEndPointMethodName(declaredAnnotation: Endpoint?): String {
        val httpMethodName = declaredAnnotation?.method
        return (httpMethodName ?: HttpMethodName.GET).name.toLowerCase()
    }

    private fun getParamName(declaredAnnotation: Endpoint?): String {
        val paramName = declaredAnnotation?.param
        return (paramName ?: "id")
    }

    private fun createEndpoint(method: Method): EndPoint {
        val types = method.parameterTypes
        val declaredAnnotation: Endpoint? = method.getDeclaredAnnotation(Endpoint::class.java)

        val methodName = getEndPointMethodName(declaredAnnotation)
        var url = method.name
        var input: String? = null
        var param: String? = null
        val hasInput = types.isNotEmpty()
        val handlerName = "$methodName${StringUtils.capitalize(url)}"

        if (hasInput) {
            val inputType = types[0]
            val inputTypeName = typeMapper.map(inputType)
            val createUrlParameterName = inputType == java.lang.String::class.java
            if (createUrlParameterName) {
                param = getParamName(declaredAnnotation)
                url += "/:$param"
            } else {
                input = simpleName(inputTypeName)
            }
        }

        return EndPoint(
            url, input, param, method.returnType.toString(),
            methodName, handlerName, isHandlerAsync(method)
        )
    }

    private fun isHandlerAsync(method: Method): Boolean {
        val declaredAnnotation: AsyncHandler? = method.getDeclaredAnnotation(AsyncHandler::class.java)
        return declaredAnnotation != null
    }

    private fun simpleName(name: String): String {
        val index = name.lastIndexOf(".")
        return if (index >= 0) name.substring(index + 1) else name
    }

}