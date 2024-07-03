import com.mylosoftworks.serializelib.Serializer
import com.mylosoftworks.serializelib.annotations.SerializeClass
import com.mylosoftworks.serializelib.annotations.SerializeField
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@SerializeClass
class Serializable {
    @SerializeField(0)
    lateinit var testVar: String

    @SerializeField(1)
    var testList: Array<Int> = arrayOf(0, 1, 2, 3)
}

fun main() {
    val inst = Serializable().apply {
        testVar = "This is a test!"
    }
    val outputStream = ByteArrayOutputStream()
    Serializer.serialize(inst, outputStream)
    val serialized = outputStream.toByteArray()

    val inputStream = ByteArrayInputStream(serialized)
    val newInst = Serializer.deserialize<Serializable>(inputStream)

    println(newInst?.testList?.get(2))

}