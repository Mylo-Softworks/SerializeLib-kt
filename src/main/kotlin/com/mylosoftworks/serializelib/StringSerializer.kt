package com.mylosoftworks.serializelib

import com.mylosoftworks.serializelib.converter.asByteArray
import com.mylosoftworks.serializelib.converter.asInt
import java.io.InputStream
import java.io.OutputStream

fun Serializer.serializeString(v: String, stream: OutputStream) {
    val bytes = v.toByteArray()
    stream.write(bytes.size.asByteArray()) // Write size header
    stream.write(bytes)
}

fun Serializer.deserializeString(stream: InputStream): String {
    val lengthBuff = ByteArray(Int.SIZE_BYTES)
    stream.read(lengthBuff)
    val length = lengthBuff.asInt()
    val stringBuff = ByteArray(length)
    stream.read(stringBuff)
    return String(stringBuff)
}