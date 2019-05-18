package com.valapay.test.router


import com.valapay.test.ErrorResponse
import com.valapay.test.utils.RouterUtils
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.ReplyException
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.eventbus.sendAwait


abstract class AbstractRouter(val vertx: Vertx) {

    private val eventBus = vertx.eventBus()

    protected fun sendAndLogErrorResponse(
        rc: RoutingContext, e: Throwable, status: HttpResponseStatus, message: String?
    ) {
        sendAndLogErrorResponse(rc, e, status.code(), message)
    }

    private fun sendAndLogErrorResponse(rc: RoutingContext, e: Throwable, code: Int, message: String?) {
        logger.error("Error during request processing {}", e, message)
        if (message != null) {
            if (message.startsWith("{\"message\":") && message.endsWith("}")) {// we have error response here
                rc.response().setStatusMessage(message).setStatusCode(code).end()
            } else {
                rc.response().setStatusCode(code).end(Json.encode(ErrorResponse(message)))
            }
        } else {
            rc.response().setStatusCode(code).end(Json.encode(ErrorResponse("Internal error")))
        }


    }

    private fun handleError(cause: Throwable, rc: RoutingContext) {
        if (cause is ReplyException && cause.message?.startsWith("{\"message\":") == true) {
            rc.response().setStatusCode(cause.failureCode()).end(cause.message)
        } else {
            sendAndLogErrorResponse(rc, cause, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.message)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    open suspend fun <T> handleBody(rc: RoutingContext, address: String, message: T) {
        try {
            val response = eventBus.sendAwait<T>(address, message!!)
            handleReply(rc, response)
        } catch (e: Throwable) {
            handleError(e, rc)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    open suspend fun handleParam(rc: RoutingContext, address: String, param: String) {
        try {
            val response = eventBus.sendAwait<String>(address, param)
            handleReply(rc, response)
        } catch (e: Throwable) {
            handleError(e, rc)
        }
    }


    companion object {
        private const val MEGABYTE = (1024 * 1024).toLong()

        private val logger = LoggerFactory.getLogger(this::class.java)

        private fun sendAndLogErrorResponse(rc: RoutingContext, status: HttpResponseStatus, message: String?) {
            logger.error("Error during request processing {}", message)
            if (message != null) {
                if (message.startsWith("{\"message\":") && message.endsWith("}")) {// we have error response here
                    rc.response().setStatusCode(status.code()).end(message)
                } else {
                    rc.response().setStatusCode(status.code()).end(Json.encode(ErrorResponse(message)))
                }
            } else {
                rc.response().setStatusCode(status.code()).end(Json.encode(ErrorResponse("Internal error")))
            }
        }

        private fun <T> handleReply(rc: RoutingContext, reply: Message<T>) {
            val body = reply.body()
            rc.response().end(RouterUtils.encodeResponse(body))
        }

    }
}
