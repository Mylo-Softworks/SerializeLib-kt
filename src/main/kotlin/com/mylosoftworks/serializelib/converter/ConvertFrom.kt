package com.mylosoftworks.serializelib.converter

fun Converter.convertFrom(bytes: ByteArray, target: Class<*>): Any? {
    return when (target) {
        java.lang.Boolean::class.java, Boolean::class.java -> return bytes[0].toInt() == 1

        java.lang.Byte::class.java, Byte::class.java -> return bytes[0]
        UByte::class.java -> return bytes[0].toUByte()

        java.lang.Short::class.java, Short::class.java -> return bytes.asShort()
        UShort::class.java -> return bytes.asUshort()
        java.lang.Character::class.java, Char::class.java -> return bytes.asChar()
        java.lang.Integer::class.java, Int::class.java -> return bytes.asInt()
        UInt::class.java -> return bytes.asUint()
        java.lang.Long::class.java, Long::class.java -> return bytes.asLong()
        ULong::class.java -> return bytes.asUlong()
        java.lang.Float::class.java, Float::class.java -> return bytes.asFloat()
        java.lang.Double::class.java, Double::class.java -> return bytes.asDouble()

        else -> null
    }
}

// 2 bytes
fun ByteArray.asShort(): Short {
    return ((this[0].toInt() and 0xFF) or
            ((this[1].toInt() and 0xFF) shl 8)).toShort()
}

fun ByteArray.asUshort(): UShort {
    return this.asShort().toUShort()
}

fun ByteArray.asChar(): Char {
    return this.asShort().toInt().toChar()
}

// 4 bytes
fun ByteArray.asInt(): Int {
    return (this[0].toInt() and 0xFF) or
            ((this[1].toInt() and 0xFF) shl 8) or
            ((this[2].toInt() and 0xFF) shl 16) or
            ((this[3].toInt() and 0xFF) shl 24)
}

fun ByteArray.asUint(): UInt {
    return this.asInt().toUInt()
}

// 8 bytes
fun ByteArray.asLong(): Long {
    return ((this[0].toLong() and 0xFF) or
            ((this[1].toLong() and 0xFF) shl 8) or
            ((this[2].toLong() and 0xFF) shl 16) or
            ((this[3].toLong() and 0xFF) shl 24) or
            ((this[4].toLong() and 0xFF) shl 32) or
            ((this[5].toLong() and 0xFF) shl 40) or
            ((this[6].toLong() and 0xFF) shl 48) or
            ((this[7].toLong() and 0xFF) shl 56))
}

fun ByteArray.asUlong(): ULong {
    return this.asLong().toULong()
}

// Floating point
fun ByteArray.asFloat(): Float {
    return Float.fromBits(this.asInt())
}

fun ByteArray.asDouble(): Double {
    return Double.fromBits(this.asLong())
}