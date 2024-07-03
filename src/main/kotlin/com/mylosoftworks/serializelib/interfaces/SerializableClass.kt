package com.mylosoftworks.serializelib.interfaces

import java.io.InputStream
import java.io.OutputStream

interface SerializableClass<T : SerializableClass<T>> {
    fun serialize(instance: T, s: OutputStream)

    fun deserialize(s: InputStream): T?
}

// Kotlin patch due to generics.
internal fun <T : SerializableClass<T>> SerializableClass<T>.genericSerialize(instance: Any, s: OutputStream) {
    serialize(instance as T, s)
}