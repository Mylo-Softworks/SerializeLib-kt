package com.mylosoftworks.serializelib.interfaces

import java.io.InputStream
import java.io.OutputStream

interface SerializableOverride<T> {
    fun serialize(target: T, s: OutputStream)

    fun deserialize(s: InputStream): T
}

// Kotlin patch due to generics.
internal fun <T : SerializableOverride<T>> SerializableOverride<T>.genericSerialize(instance: Any, s: OutputStream) {
    serialize(instance as T, s)
}