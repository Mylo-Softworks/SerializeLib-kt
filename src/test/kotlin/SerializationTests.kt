import com.mylosoftworks.serializelib.Serializer
import com.mylosoftworks.serializelib.annotations.SerializeClass
import com.mylosoftworks.serializelib.annotations.SerializeField
import com.mylosoftworks.serializelib.deserialize
import com.mylosoftworks.serializelib.serialize
import org.junit.jupiter.api.Test
import kotlin.math.PI

class SerializationTests {
    @Test
    fun BasicSerializeTest() {
        @SerializeClass
        class TestObj {
            @SerializeField(0)
            var testInt: Int = 0

            @SerializeField(1)
            var testFloat: Float = 0f

            @SerializeField(2)
            lateinit var testString: String
        }

        val testInst = TestObj().apply {
            testInt = 14
            testFloat = PI.toFloat()
            testString = "This is a test!"
        }

        val serialized = Serializer.serialize(testInst)
        val deserialized = Serializer.deserialize<TestObj>(serialized)

        assert(testInst.testFloat == deserialized?.testFloat)
        assert(testInst.testInt == deserialized?.testInt)
        assert(testInst.testString == deserialized?.testString)
    }
}