package net.podkopaev.cpsComb

import org.junit.Assert
import org.junit.Test

class CpsCombTest  {
    @Test fun testTerm() {
        val input = "x"
        val p = terminal("x")
        val result = p.parse(input, p)
        Assert.assertEquals(input, result)
    }
     @Test fun testFix() {
         val input = "++++x"
         val p = fix { e: Recognizer<Int> ->
             transp(terminal("x")) { 0 } /
                     transp(seq(terminal("+"), e))
                        { p -> 1 + p.second}
         }
         val result = p.parse(input, p)
         Assert.assertEquals(4, result)
     }

     @Test fun testLeftRecursiveRule() {
         val input = "bbbbb"
         // Left recursive rule.
         val p = fix { s: Recognizer<Int> -> transp(terminal("b")) { 1 } /
                 transp(seq(s, terminal("b"))) { p -> 1 + p.first } }
         val result = p.parse(input, p)
         Assert.assertEquals(input.length, result)
     }

    @Test fun testNumber() {
        val input = "123"
        val p = number
        val result = p.parse(input, p)
        Assert.assertEquals(123, result)
    }

    @Test fun testSymbol() {
        val input = "abcd"
        val p = symbol
        val result = p.parse(input, p)
        Assert.assertEquals("abcd", result)
    }
}