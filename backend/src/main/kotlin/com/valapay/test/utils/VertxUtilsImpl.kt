package com.valapay.test.utils

import com.valapay.test.ErrorResponse
import com.valapay.test.VerticleHandlerResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.executeBlockingAwait
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class VertxUtilsImpl : VertxUtils {

    private val logger = LoggerFactory.getLogger(VertxUtilsImpl::class.java)

    @Autowired
    private lateinit var vertx: Vertx

    @Suppress("TooGenericExceptionCaught")
    override suspend fun <M> executeHandler(blockingCode: () -> VerticleHandlerResponse<M>, message: Message<*>) {
        try {
            val handlerResponse = vertx.executeBlockingAwait(handler(blockingCode))!!
            handleResultAndError(handlerResponse, message)
        } catch (e: Throwable) {
            failOnException(e, message)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun <M> handler(blockingCode: () -> M): (Future<M>) -> Unit {
        return { f ->
            try {
                f.complete(blockingCode())
            } catch (e: Throwable) {
                f.fail(e)
                logger.error("handler failed", e)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun <M> executeSuspendHandler(
        blockingCode: suspend () -> VerticleHandlerResponse<M>, message: Message<*>
    ) {
        try {
            val handlerResponse = GlobalScope.async { blockingCode() }.await()
            handleResultAndError(handlerResponse, message)
        } catch (e: Throwable) {
            failOnException(e, message)
        }
    }


    private fun <M> handleResultAndError(handlerResponse: VerticleHandlerResponse<M>, message: Message<*>) {
        try {
            if (handlerResponse.failed) {
                message.fail(handlerResponse.errorCode, Json.encode(ErrorResponse(handlerResponse.error)))
            } else {
                message.reply(handlerResponse.response)
            }
        } catch (e: IllegalArgumentException) {//Usually - no such codec
            message.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), Json.encode(ErrorResponse(e.message!!)))
        }
    }

    private fun failOnException(exception: Throwable, message: Message<*>) {
        val m = exception.message ?: "Internal Error"

        logger.error("Handling error", exception)
        message.fail(
            HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), Json.encode(ErrorResponse(m))
        )
    }


    private fun fail(message: Message<*>, status: HttpResponseStatus, m: String?) {
        message.fail(status.code(), Json.encode(ErrorResponse(m ?: "")))
    }


    override suspend fun <T> execute(blockingMethod: () -> T): T {
        return vertx.executeBlockingAwait(handler(blockingMethod))!!
    }

    companion object {
        private const val INTERNAL_ERROR = "Internal Error"
    }

}
