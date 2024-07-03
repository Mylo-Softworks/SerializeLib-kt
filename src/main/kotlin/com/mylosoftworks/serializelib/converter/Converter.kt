package com.mylosoftworks.serializelib.converter

private val supportedTypes = listOf(java.lang.Boolean::class.java, Boolean::class.java,
    java.lang.Byte::class.java, Byte::class.java, UByte::class.java,
    java.lang.Short::class.java, Short::class.java, UShort::class.java,
    java.lang.Character::class.java, Char::class.java,
    java.lang.Integer::class.java, Int::class.java, UInt::class.java,
    java.lang.Long::class.java, Long::class.java, ULong::class.java,
    java.lang.Float::class.java, Float::class.java,
    java.lang.Double::class.java, Double::class.java)

fun isSupportedType(type: Class<*>): Boolean = supportedTypes.contains(type)

/**
 * A converter for primitive types.
 *
 * Source code for conversions is contained in ConvertFrom and ConvertTo
 */
object Converter {
    inline fun <reified T : Any> convertsCorrectly(obj: T): Boolean {
        return obj == convertTo(obj)?.let { convertFrom(it, T::class.java) }
    }

    fun sizeOf(klass: Class<*>): Int {
        return when (klass) {
            Boolean::class.java, java.lang.Boolean::class.java, java.lang.Byte::class.java, Byte::class.java -> 1
            java.lang.Short::class.java, Short::class.java, UShort::class.java, java.lang.Character::class.java, Char::class.java -> Short.SIZE_BYTES // 2
            java.lang.Integer::class.java, Int::class.java, UInt::class.java,
                 java.lang.Float::class.java, Float::class.java-> Int.SIZE_BYTES // 4
            java.lang.Long::class.java, Long::class.java, ULong::class.java,
                 java.lang.Double::class.java, Double::class.java-> Long.SIZE_BYTES // 8
            else -> error("Not convertable type")
        }
    }
}