package com.valapay.test.verticles

import com.valapay.test.EmptyMessage
import com.valapay.test.VerticleHandlerResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.springframework.data.jpa.repository.JpaRepository

open class AbstractVerticle<T>(protected val repo: JpaRepository<T, Long>) : CoroutineVerticle() {

    protected open fun createHandler(message: T): VerticleHandlerResponse<T> {
        val toSave = beforeNewEntityHook(message)
        val response = repo.save(toSave)
        val afterSave = afterNewEntityHook(response)
        return VerticleHandlerResponse.ok(afterSave)
    }

    protected open fun deleteHandler(id: Long): VerticleHandlerResponse<JsonObject> {

        deleteEntityHook(id)
        repo.deleteById(id)
        return VerticleHandlerResponse.ok(JsonObject())
    }

    protected open fun updateHandler(message: T): VerticleHandlerResponse<T> {
        val toSave = beforeUpdateEntityHook(message)
        val response = repo.save(toSave)
        val afterSave = afterUpdateEntityHook(response)
        return VerticleHandlerResponse.ok(afterSave)
    }

    protected open fun getHandler(id: String): VerticleHandlerResponse<T> {
        val findById = repo.findById(id.toLong())
        return if (findById.isPresent)
            VerticleHandlerResponse.ok(loadedEntityHook(findById.get()))
        else VerticleHandlerResponse.error(HttpResponseStatus.NOT_FOUND, "Not found")
    }

    protected open fun getAllHandler(message: EmptyMessage): VerticleHandlerResponse<JsonArray> {
        return VerticleHandlerResponse.ok(
            JsonArray(
                loadedEntitiesHook(repo.findAll()).map {
                    JsonObject(
                        Json.encode(it)
                    )
                })
        )
    }


    protected open fun beforeUpdateEntityHook(entity: T): T {
        return entity
    }

    protected open fun afterUpdateEntityHook(entity: T): T {
        return entity
    }

    protected open fun beforeNewEntityHook(entity: T): T {
        return entity
    }

    protected open fun afterNewEntityHook(entity: T): T {
        return entity
    }

    protected open fun loadedEntityHook(entity: T): T {
        return entity
    }

    protected open fun loadedEntitiesHook(entities: List<T>): List<T> {
        return entities
    }


    protected open fun deleteEntityHook(id: Long) {

    }
}
