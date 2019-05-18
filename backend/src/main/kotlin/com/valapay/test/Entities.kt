package com.valapay.test

import com.valapay.test.annotations.EmptyConstructorMessage
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future


@EmptyConstructorMessage
data class ErrorResponse(val message:String)

/**
 * For event bus if there is no input
 */
object EmptyMessage

class VerticleHandlerResponse<T> private constructor(
    val failed: Boolean, val response: T?, val errorCode: Int, val error: String
) {
    companion object {
        fun <T> ok(response: T): VerticleHandlerResponse<T> {
            return VerticleHandlerResponse(false, response, 0, "")
        }

        fun <T> error(errorCode: HttpResponseStatus, error: String): VerticleHandlerResponse<T> {
            return VerticleHandlerResponse(true, null, errorCode.code(), error)
        }

        fun <T> error(errorCode: HttpResponseStatus): VerticleHandlerResponse<T> {
            return VerticleHandlerResponse(
                true, null, errorCode.code(), errorCode.reasonPhrase()
            )
        }

        fun <T,M> copyError(other:VerticleHandlerResponse<M>):  VerticleHandlerResponse<T>{
            return VerticleHandlerResponse(true,null,other.errorCode,other.error)
        }
        fun <T,M> copyFutureError(other:VerticleHandlerResponse<M>): Future<VerticleHandlerResponse<T>> {
            return Future.succeededFuture(VerticleHandlerResponse<T>(true,null,other.errorCode,other.error))
        }
    }
}