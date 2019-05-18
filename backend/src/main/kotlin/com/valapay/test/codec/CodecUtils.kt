package com.valapay.test.codec

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json

object CodecUtils {
    fun writeString(b: Buffer, s: String) {
        b.appendInt(s.length)
        b.appendString(s)
    }

    fun writeJson(b: Buffer, s: Any) {
        writeString(b, s.javaClass.name)
        writeString(b, Json.encode(s))
    }

    fun readString(pos: Int, b: Buffer): StringAndPositionSkip {
        val length = b.getInt(pos)
        val bytes = b.getBytes(pos + INT_LENGTH, pos + length + INT_LENGTH)
        return StringAndPositionSkip(String(bytes, Charsets.UTF_8), length + INT_LENGTH)
    }

    fun readJson(pos: Int, b: Buffer): ObjectAndPositionSkip {
        val className = readString(pos, b)
        val json = readString(pos + className.skipBytes, b)
        return ObjectAndPositionSkip(
            Json.decodeValue(json.s, Class.forName(className.s)), className.skipBytes + json.skipBytes
        )
    }

    internal const val INT_LENGTH = 4

}

data class StringAndPositionSkip(val s: String, val skipBytes: Int)
data class ObjectAndPositionSkip(val s: Any, val skipBytes: Int)