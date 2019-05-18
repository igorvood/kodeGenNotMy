package com.valapay.test.codegen.descriptors

import com.valapay.test.codegen.typemappers.TypeMapper
import com.valapay.test.annotations.*
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner

class EntitiesCreator(typeMapper: TypeMapper, frontendAnnoPackage:String) {

    private val messagesDescriptor =
        MessagesCreator(typeMapper, frontendAnnoPackage)

    fun createEntities(entitiesPackage: String): List<Entity> {
        val reflections = Reflections(entitiesPackage, SubTypesScanner(false))
        val types = reflections.getSubTypesOf(Object::class.java)
        return types.map { createEntity(it) }
    }

    fun createEntityRestEndpoints(entity: Entity): List<EndPoint> {
        val name = entity.name

        val url = name.toLowerCase()
        val endpoints: MutableList<EndPoint> = mutableListOf()
        if (entity.crudFeatures.create) {
            endpoints.add(
                EndPoint(url, name, null, name, "post", "handleNew$name", false)
            )
        }
        if (entity.crudFeatures.get) {
            endpoints.add(
                EndPoint(
                    "$url/:id", null, "id", name, "get", "handleGet$name", false
                )
            )
        }
        if (entity.crudFeatures.update) {
            endpoints.add(
                EndPoint(url, name, null, name, "put", "handleUpdate$name", false)
            )
        }
        if (entity.crudFeatures.delete) {
            endpoints.add(
                EndPoint(
                    "$url/:id", null, "id", "", "delete", "handleDelete$name", false
                )
            )
        }

        if (entity.crudFeatures.list) {
            if (entity.parents.isEmpty()) {
                endpoints.add(
                    EndPoint(
                        url, null, null, "List<$name>", "get", "handleGetAllFor$name", false
                    )
                )
            }
        }
        entity.children.forEach {
            endpoints.add(
                EndPoint(
                    "$url/:id/${it.name.toLowerCase()}", null, "id", "List<$name>", "get",
                    "handleGet${it.name}For$name", false
                )
            )
        }
        return endpoints
    }

    private fun createEntity(aClass: Class<*>): Entity {
        return Entity(
            aClass.simpleName, getParents(aClass),
            isVerticleAbstract(aClass),
            shouldGenerateCrud(aClass),
            messagesDescriptor.createFields(aClass), listOf()
        )
    }

    private fun isVerticleAbstract(aClass: Class<*>): Boolean {
        return aClass.getDeclaredAnnotation(AbstractImplementation::class.java) != null
    }

    private fun getParents(aClass: Class<*>): List<String> {
        return aClass.getDeclaredAnnotation(ChildOf::class.java)?.parents?.map { it.simpleName }?.requireNoNulls()
            ?: listOf()
    }

    private fun shouldGenerateCrud(aClass: Class<*>): CrudFeatures {
        val listAnno = aClass.getDeclaredAnnotation(GenerateList::class.java)
        val createAnno = aClass.getDeclaredAnnotation(GenerateCreate::class.java)
        val getAnno = aClass.getDeclaredAnnotation(GenerateGetById::class.java)
        val updateAnno = aClass.getDeclaredAnnotation(GenerateUpdate::class.java)
        val deleteAnno = aClass.getDeclaredAnnotation(GenerateDelete::class.java)
        return CrudFeatures(
            list = listAnno != null,
            create = createAnno != null,
            update = updateAnno != null,
            delete = deleteAnno != null,
            get = getAnno != null
        )
    }
}