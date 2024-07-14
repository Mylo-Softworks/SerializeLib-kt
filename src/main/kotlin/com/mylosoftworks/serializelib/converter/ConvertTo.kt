package com.mylosoftworks.serializelib.converter

import com.mylosoftworks.serializelib.convertEndianNess

fun Converter.convertTo(obj: Any): ByteArray? {
    return when (obj) {
        is Boolean -> if (obj) byteArrayOf(1) else byteArrayOf(0)

        is Byte -> byteArrayOf(obj)
        is UByte -> byteArrayOf(obj.toByte())
        is Short -> obj.asByteArray()
        is Char -> obj.asByteArray()
        is UShort -> obj.asByteArray()
        is Int -> obj.asByteArray()
        is UInt -> obj.asByteArray()
        is Long -> obj.asByteArray()
        is ULong -> obj.asByteArray()
        is Float -> obj.asByteArray()
        is Double -> obj.asByteArray()

        else -> null
    }?.convertEndianNess()
}

// 2 bytes

fun Short.asByteArray(): ByteArray {
    val asInt = this.toInt()
    return byteArrayOf(
        (asInt and 0xFF).toByte(),
        ((asInt shr 8) and 0xFF).toByte()
    )
}

fun UShort.asByteArray(): ByteArray {
    return this.toShort().asByteArray()
}

fun Char.asByteArray(): ByteArray {
    return this.code.toShort().asByteArray()
}

// 4 bytes
fun Int.asByteArray(): ByteArray {
    return byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte()
    )
}

fun UInt.asByteArray(): ByteArray {
    return this.toInt().asByteArray()
}

// 8 bytes
fun Long.asByteArray(): ByteArray {
    return byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 32) and 0xFF).toByte(),
        ((this shr 40) and 0xFF).toByte(),
        ((this shr 48) and 0xFF).toByte(),
        ((this shr 56) and 0xFF).toByte()
    )
}

fun ULong.asByteArray(): ByteArray {
    return this.toLong().asByteArray()
}

// Floating point
fun Float.asByteArray(): ByteArray {
    return this.toBits().asByteArray()
}

fun Double.asByteArray(): ByteArray {
    return this.toBits().asByteArray()
}