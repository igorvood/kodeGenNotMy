package com.valapay.test.codec

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec

@Suppress("UNCHECKED_CAST")
class JsonListCodec<T> : MessageCodec<ArrayList<T>, ArrayList<T>> {

    override fun encodeToWire(buffer: Buffer, t: ArrayList<T>) {
        val len = t.size
        buffer.appendInt(len)

        t.forEach {
            CodecUtils.writeJson(buffer, it as Any)
        }

    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): ArrayList<T> {
        val len = buffer.getInt(pos)
        var currentPos = pos + CodecUtils.INT_LENGTH
        val list = (0 to len).toList().map {
            val o = CodecUtils.readJson(currentPos, buffer)
            currentPos += o.skipBytes
            o.s as T
        }
        val ret = ArrayList<T>()
        ret.addAll(list)
        return ret

    }

    override fun transform(t: ArrayList<T>): ArrayList<T> {
        return t
    }

    override fun name(): String {
        return "List"
    }

    override fun systemCodecID(): Byte {
        return -1
    }


}
