package com.valapay.test.codegen.descriptors

data class EndPoint(
    val url: String, val input: String?, val param: String?, val output: String, val method: String,
    val handler: String, val asyncHandler: Boolean
)

data class Router(val name: String, val url: String, val endpoints: List<EndPoint>)

class Entity(
    name: String, val parents: List<String>, val abstractVerticle: Boolean,
    val crudFeatures: CrudFeatures, fields: List<Field>, var children: List<Entity>
) : Message(name, fields) {

    fun shouldGenerateRouterAndVerticle(): Boolean {
        return crudFeatures.generateRouterAndVerticle()
    }

    override fun toString(): String {
        return "Entity(parents=$parents, abstractVerticle=$abstractVerticle, crudFeatures=$crudFeatures, children=$children)"
    }

}

data class CrudFeatures(
    val list: Boolean, val create: Boolean, val update: Boolean, val delete: Boolean,
    val get: Boolean
) {

    fun generateRouterAndVerticle(): Boolean {
        return list || create || update || delete || get
    }
}


open class Message(val name: String, val fields: List<Field>)

data class Field(val name: String, val type: String, val validators: List<Annotation>, val findBy: Boolean)

