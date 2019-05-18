package com.valapay.test.annotations

import kotlin.reflect.KClass

/* Contains a number of endpoints. We will generate Vert.x router or Spring MVC controller from it*/
annotation class EndpointController(val url:String)

/* Endpoint inside a controller. Concrete URI and HTTP method. May be has query param */
annotation class Endpoint(val method: HttpMethodName, val param: String = "")

/* For empty constructor generation */
annotation class EmptyConstructorMessage

/* Make abstract implementation method for endpoint logic asynchronous */
annotation class AsyncHandler


/* All the next annotations are for Entities only:*/
annotation class GenerateCreate
annotation class GenerateUpdate
annotation class GenerateGetById
annotation class GenerateList
annotation class GenerateDelete

/* Make CRUD implementation abstract, so that we will override it*/
annotation class AbstractImplementation

/* Generate search by this field in DAO layer */
annotation class FindBy

/* This entity is child of another entity, so generate end point like
 /parent/$id/child to bring all children of concrete parent
 instead of
 /child - bring all entities of this type
 */
annotation class ChildOf(vararg val parents: KClass<*>)

enum class HttpMethodName {
    POST,PUT,GET,DELETE
}


