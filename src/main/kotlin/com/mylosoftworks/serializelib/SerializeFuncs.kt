package com.mylosoftworks.serializelib

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream

inline fun <reified T> Serializer.serialize(obj: T): ByteArray {
    return serialize(obj, T::class.java)
}

fun Serializer.serialize(obj: Any?, klass: Class<*>): ByteArray {
    val stream = ByteArrayOutputStream()
    serialize(obj, klass, stream)
    return stream.toByteArray()
}

inline fun <reified T> Serializer.deserialize(bytes: ByteArray): T? {
    return deserialize(bytes, T::class.java) as T?
}

fun Serializer.deserialize(bytes: ByteArray, klass: Class<*>): Any? {
    val stream = ByteArrayInputStream(bytes)
    return deserialize(stream, klass)
}

inline fun <reified T> Serializer.serializeToFile(obj: T, file: String) {
    Serializer.serializeToFile(obj, T::class.java, file)
}

fun Serializer.serializeToFile(obj: Any?, klass: Class<*>, file: String) {
    val stream = FileOutputStream(file)
    serialize(obj, klass, stream)
}

inline fun <reified T> Serializer.deserializeFromFile(file: String): T? {
    return deserializeFromFile(file, T::class.java) as T?
}

fun Serializer.deserializeFromFile(file: String, klass: Class<*>): Any? {
    val stream = FileInputStream(file)
    return deserialize(stream, klass)
}