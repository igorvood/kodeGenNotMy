package com.valapay.test.utils

import io.vertx.core.json.Json

object RouterUtils {
    fun <T> encodeResponse(response: T): String {
        return "{ \"data\": ${Json.encode(response)} }"
    }
}