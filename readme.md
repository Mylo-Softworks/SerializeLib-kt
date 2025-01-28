[![](https://www.jitpack.io/v/Mylo-Softworks/SerializeLib-kt.svg)](https://www.jitpack.io/#Mylo-Softworks/SerializeLib-kt)

[![Read the Docs](https://img.shields.io/readthedocs/serializelib)](https://serializelib.readthedocs.io/)

# SerializeLib-kt
A library for serializing and deserializing in Kotlin/JVM

## Supported types
* Primitives
    * bool, string, byte, short, int, long, float, double, decimal (and unsigned variants from Kotlin).
* Arrays (But not lists, due to type erasure)
    * Arrays can contain any supported types.
    * **Note**: Arrays with value of `null` will be deserialized as an empty Array.
* Objects
    * Any object which has the @SerializeClass annotation can be serialized as a field as well.
    * Objects with value of `null` will be deserialized as `null` as expected.

## Usage
### Defining a serializable object

1. With attributes (automatic)
```kotlin
import com.mylosoftworks.serializelib.annotations.SerializeClass
import com.mylosoftworks.serializelib.annotations.SerializeField

@SerializeClass
class SerializationExample {
    @SerializeField(0) var exampleBool: Boolean = false
}
```
2. With interface (manual)  
   If you want to manually serialize data which can't be serialized yet, you can read/write the stream.  
   **Make 100% sure that you read EXACTLY as many bytes as you write. Failure to do so will fail to deserialize.**
```kotlin
import com.mylosoftworks.serializelib.Serializer
import com.mylosoftworks.serializelib.interfaces.SerializableClass

class ManualSerializeClass : SerializableClass<ManualSerializeClass> {
    var number: Int = 0

    override fun serialize(s: OutputStream) {
        Serializer.serializeValue(number, s)
    }

    override fun deserialize(s: InputStream): ManualSerializeClass {
        number = Serializer.deserializeValue<Int>(s)!!

        return this
    }
}
```

### Serializing the object

```kotlin
import com.mylosoftworks.serializelib.Serializer
import java.io.ByteArrayOutputStream

// Create an object to serialize
val exampleObject = SerializationExample().apply {
    ExampleBool = true
}

// Serialize to a stream
val stream = ByteArrayOutputStream()
Serializer.Serialize(exampleObject, stream)

// Serialize to a ByteArray
val bytes = Serializer.Serialize(exampleObject)

// Serialize and write to file
Serialize.SerializeToFile(exampleObject, "filename.bin")
```

### Deserializing the object

```kotlin
import com.mylosoftworks.serializelib.Serializer
import java.io.ByteArrayInputStream

// Deserialize from a stream
val stream = ByteArrayInputStream(ByteArray(0)) // In practice, this should be a stream with the serialized bytes
val exampleObject = Serializer.Deserialize<SerializationExample>(stream)

// Deserialize from a byte[]
val bytes = ByteArray(0) // In practice, this should be a byte array with the serialized bytes
val exampleObject = Serializer.Deserialize<SerializationExample>(bytes)

// Deserialize from a file
val exampleObject = Serializer.DeserializeFromFile<SerializationExample>("filename.bin")
```
