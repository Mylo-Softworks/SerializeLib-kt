import com.mylosoftworks.serializelib.converter.Converter
import org.junit.jupiter.api.Test

internal class ConverterTests {
    @Test
    fun testBool() {
        assert(Converter.convertsCorrectly(true))
    }

    @Test
    fun testByte() {
        assert(Converter.convertsCorrectly(112.toByte()))
    }

    @Test
    fun testShort() {
        assert(Converter.convertsCorrectly(45.toShort()))
    }

    @Test
    fun testUShort() {
        assert(Converter.convertsCorrectly(70242.toUShort()))
    }

    @Test
    fun testChar() {
        assert(Converter.convertsCorrectly('a'))
    }


    @Test
    fun testInteger() {
        assert(Converter.convertsCorrectly(163692134))
    }

    @Test
    fun testLong() {
        assert(Converter.convertsCorrectly(123456789123456789L))
    }

    @Test
    fun testFloat() {
        assert(Converter.convertsCorrectly(3.14F))
    }

    @Test
    fun testDouble() {
        assert(Converter.convertsCorrectly(1.123))
    }
}