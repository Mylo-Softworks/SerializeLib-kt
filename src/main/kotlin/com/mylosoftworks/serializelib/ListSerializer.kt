package com.mylosoftworks.serializelib

import com.mylosoftworks.serializelib.converter.asByteArray
import com.mylosoftworks.serializelib.converter.asInt
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Array

fun Serializer.serializeList(list: List<*>, klass: Class<*>, stream: OutputStream) {
    val countSerialized = list.size.asByteArray()
    stream.write(countSerialized) // Item count

    for (item in list) {
        serializeValue(item, klass, stream)
    }
}

fun Serializer.deserializeList(stream: InputStream, klass: Class<*>): kotlin.Array<Any?> {
    val itemBuffer = ByteArray(Int.SIZE_BYTES)
    stream.read(itemBuffer)
    val itemCount = itemBuffer.asInt()

    val arr = Array.newInstance(klass, itemCount) as kotlin.Array<Any?>

    repeat(itemCount) {
        arr[it] = deserializeValue(stream, klass)
    }

    return arr
}