package podkopaev.net

import org.junit.Assert
import org.junit.Test

class BasicCombTest {
    @Test fun test0() {
        val parser = digit
        Assert.assertEquals('1', parser.get("1"))
    }

    @Test fun test2() {
        val parser = many1(digit)
        Assert.assertEquals(
                listOf('1', '2', '3'),
                parser.get("123")
        )
    }

    @Test fun test3() {
        val parser = digit - digit
        Assert.assertEquals(
                '2',
                parser.get("12")
        )
    }

    @Test fun test4() {
        val parser = combinep(digit, digit) {
            x, y -> y.toString() + x.toString()
        }
        Assert.assertEquals(
                "21",
                parser.get("12")
        )
    }

    @Test fun test1() {
        val parser = number
        Assert.assertEquals(123, parser.get("123"))
    }
}