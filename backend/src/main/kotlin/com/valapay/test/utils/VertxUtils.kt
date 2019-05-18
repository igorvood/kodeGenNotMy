package com.valapay.test.utils

import com.valapay.test.VerticleHandlerResponse
import io.vertx.core.eventbus.Message
import org.springframework.stereotype.Service

@Service
interface VertxUtils {

    suspend fun <M> executeHandler(blockingCode: () -> VerticleHandlerResponse<M>, message: Message<*>)

    suspend fun <M> executeSuspendHandler(blockingCode: suspend () -> VerticleHandlerResponse<M>, message: Message<*>)

    suspend fun <T> execute(blockingMethod: () -> T): T
}