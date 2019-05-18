package com.valapay.test.codec

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec

@Suppress("UNCHECKED_CAST")
class JsonCodec<T>(private val aClass: Class<T>) : MessageCodec<T, T> {

    override fun encodeToWire(buffer: Buffer, t: T) {
        CodecUtils.writeJson(buffer, t as Any)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): T {
        return CodecUtils.readJson(pos, buffer).s as T
    }

    override fun transform(t: T): T {
        return t
    }

    override fun name(): String {
        return aClass.name
    }

    override fun systemCodecID(): Byte {
        return -1
    }

}
