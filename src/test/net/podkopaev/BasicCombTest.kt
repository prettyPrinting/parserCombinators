package net.podkopaev

import org.junit.Assert
import org.junit.Test

class BasicCombTest {
    @Test fun test0() {
        val parser = digit
        Assert.assertEquals(listOf('1'), parser.get("1"))
    }

    @Test fun test1() {
        val parser = number
        Assert.assertEquals(listOf("123"), parser.get("123"))
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
                listOf('2'),
                parser.get("12")
        )
    }

    @Test fun test4() {
        val parser = combinep(digit, digit) {
            x, y -> y.toString() + x.toString()
        }
        Assert.assertEquals(
                listOf("21"),
                parser.get("12")
        )
    }

    @Test fun test5() {
        val parser = disjp(digit, alpha)
        Assert.assertEquals(listOf('a'), parser.get("a"))
    }


    @Test fun test7() {
        val parser = disjp(word, symbol)
        Assert.assertEquals(listOf("abcd123"), parser.get("abcd123"))
    }

    @Test fun test8() {
        val parser = conjp(symbol, word)
        Assert.assertEquals(null, parser.get("abcd123"))
    }

    @Test fun test9() {
        val parser = conjp(word, symbol)
        Assert.assertEquals(listOf("qwrt"), parser.get("qwrt"))
    }

    @Test fun test10() {
        val parser = disjp(digit, alphaOrDigit)
        Assert.assertEquals(listOf('a'), parser.get("a"))
    }

    @Test fun test11() {
        val parser = conjp(digit, alphaOrDigit)
        Assert.assertEquals(null, parser.get("a"))
    }

    @Test fun test12() {
        val parser = conjp(digit, alphaOrDigit)
        Assert.assertEquals(listOf('1'), parser.get("1"))
    }
}