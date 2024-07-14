package com.mylosoftworks.serializelib

import com.mylosoftworks.serializelib.annotations.SerializeClass
import com.mylosoftworks.serializelib.annotations.SerializeField
import com.mylosoftworks.serializelib.converter.Converter
import com.mylosoftworks.serializelib.converter.convertFrom
import com.mylosoftworks.serializelib.converter.convertTo
import com.mylosoftworks.serializelib.converter.isSupportedType
import com.mylosoftworks.serializelib.interfaces.SerializableClass
import com.mylosoftworks.serializelib.interfaces.SerializableOverride
import com.mylosoftworks.serializelib.interfaces.genericSerialize
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.nio.ByteOrder

fun ByteArray.convertEndianNess(isBigEndian: Boolean? = null): ByteArray {
    @Suppress("NAME_SHADOWING") val isBigEndian = isBigEndian ?: Serializer.IsBigEndian
    if (isBigEndian == Serializer.UseBigEndian) return this
    return this.reversedArray()
}

object Serializer {

    val IsBigEndian: Boolean get() = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
    var UseBigEndian: Boolean = IsBigEndian

    inline fun <reified T> serialize(obj: T, stream: OutputStream) {
        val klass = T::class.java
        serialize(obj, klass, stream)
    }

    fun isManualSerialized(t: Class<*>) = t.interfaces.contains(SerializableClass::class.java)

    val overrides = HashMap<Class<*>, Pair<(Any?, OutputStream) -> Unit, (InputStream) -> Any?>>()

    inline fun <reified T> registerOverride(o: SerializableOverride<T>?) {
        val klass = T::class.java
        if (o == null) {
            overrides.remove(klass)
            return
        }
        overrides[klass] = {obj: Any?, stream: OutputStream ->
            o.serialize(obj as T, stream)
        } to {stream: InputStream ->
            o.deserialize(stream)
        }
    }

    inline fun <reified T : SerializableOverride<TS>, reified TS> registerOverride() {
        val inst = T::class.java.newInstance()
        overrides[TS::class.java] = {obj: Any?, stream: OutputStream ->
            inst.serialize(obj as TS, stream)
        } to {stream: InputStream ->
            inst.deserialize(stream)
        }
    }

    fun isRegisteredOverride(klass: Class<*>) = overrides.containsKey(klass)

    fun serialize(obj: Any?, klass: Class<*>, stream: OutputStream, noError: Boolean = false) {
        if (isManualSerialized(klass)) {
            (obj as SerializableClass<*>).genericSerialize(stream)
            return
        }

        if (isRegisteredOverride(klass)) {
            overrides[klass]!!.first(obj, stream)
            return
        }

        if (!klass.isAnnotationPresent(SerializeClass::class.java))
            if (noError) return
            else throw IllegalArgumentException("This type does not have a SerializeClassAttribute")

        if (obj == null) {
            stream.write(0)
            return
        }
        stream.write(1)

        val fields = mutableListOf<Pair<Int, Field>>()
        for (field in klass.fields) {
            if (!field.isAnnotationPresent(SerializeField::class.java)) continue
            fields.add(field.getAnnotation(SerializeField::class.java).order to field)
        }

        for (field in fields.sortedBy { it.first }.map { it.second }) {
            serializeValue(field.get(obj), field.type, stream)
        }

        val methods = mutableListOf<Pair<Int, Method>>()
        for (method in klass.methods) {
            if (!method.isAnnotationPresent(SerializeField::class.java)) continue
            if (method.name.endsWith("\$annotations")) { // 12 chars
                val baseName = method.name.substring(0, method.name.length - 12)
                val getter = klass.getMethod(baseName)

                methods.add(method.getAnnotation(SerializeField::class.java).order to getter)
            }
        }

        for (method in methods.sortedBy { it.first }.map { it.second }) {
            serializeValue(method.invoke(obj), method.returnType, stream)
        }

        stream.flush()
    }

    inline fun <reified T> serializeValue(value: T, stream: OutputStream) {
        serializeValue(value, T::class.java, stream)
    }

    fun serializeValue(value: Any?, klass: Class<*>, stream: OutputStream) {

        if (isSupportedType(klass)) {
            stream.write(Converter.convertTo(value ?: klass.newInstance())!!)
            return
        }

        if (klass == java.lang.String::class.java) {
            Serializer.serializeString(value as String, stream)
            return
        }

        if (isGenericCollection(klass)) {
            error("Collections cannot be serialized due to type erasure, use arrays instead")
        }

        if (isGenericArray(klass)) {
            val (iterable, iterType) = asIterable(value)
            val iterator = iterable?.iterator()
            if (iterator == null || !iterator.hasNext()) { // list is zero or empty
                stream.write(ByteArray(4)) // Write 0 as int
                return
            }
            val outList = mutableListOf<Any?>()
            iterator.forEachRemaining {
                outList.add(it)
            }
            serializeList(outList, iterType!!, stream)
        }

        if (value == null) {
            stream.write(0)
            return
        }

        serialize(value, klass, stream, true) // Serializing objects
    }

    inline fun <reified T> deserialize(stream: InputStream): T? {
        return deserialize(stream, T::class.java) as T?
    }

    fun deserialize(stream: InputStream, klass: Class<*>, noError: Boolean = false): Any? {
        if (isManualSerialized(klass)) {
            return (klass.newInstance() as SerializableClass<*>).deserialize(stream)
        }

        if (isRegisteredOverride(klass)) {
            return overrides[klass]!!.second(stream)
        }

        if (!klass.isAnnotationPresent(SerializeClass::class.java))
            if (noError) return null
            else throw IllegalArgumentException("This type does not have a SerializeClassAttribute")

        if (stream.read() == 0) return null

        var objInst = klass.newInstance()

        val fields = mutableListOf<Pair<Int, Field>>()
        for (field in klass.fields) {
            if (!field.isAnnotationPresent(SerializeField::class.java)) continue
            fields.add(field.getAnnotation(SerializeField::class.java).order to field)
        }

        for (field in fields.sortedBy { it.first }.map { it.second }) {
            field.set(objInst, deserializeValue(stream, field.type))
        }

        val methods = mutableListOf<Triple<Int, Method, Class<*>>>()
        for (method in klass.methods) {
            if (!method.isAnnotationPresent(SerializeField::class.java)) continue
            if (method.name.endsWith("\$annotations")) { // 12 chars
                val baseName = method.name.substring(0, method.name.length - 12)
                val getter = klass.getMethod(baseName)
                val setter = klass.getMethod(baseName.replaceFirst("get", "set"), getter.returnType)

                methods.add(Triple(method.getAnnotation(SerializeField::class.java).order, setter, getter.returnType))
            }
        }

        for ((_, method, methodClass) in methods.sortedBy { it.first }) {
            val result = deserializeValue(stream, methodClass)
            method.invoke(objInst, result)
        }

        return objInst
    }

    inline fun <reified T> deserializeValue(stream: InputStream): T? {
        return deserializeValue(stream, T::class.java) as T?
    }

    fun deserializeValue(stream: InputStream, klass: Class<*>): Any? {

        if (isSupportedType(klass)) {
            val buffer = ByteArray(Converter.sizeOf(klass))
            stream.read(buffer)
            return Converter.convertFrom(buffer, klass) // deserialize
        }

        if (klass == java.lang.String::class.java) {
            return Serializer.deserializeString(stream)
        }

        if (isGenericCollection(klass)) {
            error("Collections cannot be serialized due to type erasure, use arrays instead")
        }

        if (isGenericArray(klass)) {
            val arrayType = klass.componentType
            val list = deserializeList(stream, arrayType)
            return list
        }

        return deserialize(stream, klass, true)
    }

    private fun isGenericCollection(klass: Class<*>): Boolean {
        return Iterable::class.java.isAssignableFrom(klass)
    }

    private fun isGenericArray(klass: Class<*>): Boolean {
        return klass.isArray
    }

    private fun asIterable(obj: Any?): Pair<Iterable<*>?, Class<*>?> {
        return when (obj) {
            is Array<*> -> obj.asIterable()
            is Iterable<*> -> obj
            else -> null
        } to getIterTypeFromClass(obj?.javaClass)
    }

    private fun getIterTypeFromClass(klass: Class<*>?): Class<*>? {
        return when {
            klass == null -> null
            isGenericArray(klass) -> klass.componentType
//            isGenericCollection(klass) -> ((klass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as TypeVariableImpl<*>)
            else -> null
        }
    }
}

inline fun <reified T> Class<Iterable<T>>.getGenericType(): Class<T> {
    return T::class.java
}