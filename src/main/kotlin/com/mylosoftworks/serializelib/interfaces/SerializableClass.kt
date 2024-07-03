package com.mylosoftworks.serializelib.interfaces

import java.io.InputStream
import java.io.OutputStream

interface SerializableClass<T : SerializableClass<T>> {
    fun serialize(s: OutputStream)

    fun deserialize(s: InputStream): T?
}

// Kotlin patch due to generics.
internal fun <T : SerializableClass<T>> SerializableClass<T>.genericSerialize(s: OutputStream) {
    serialize(s)
}