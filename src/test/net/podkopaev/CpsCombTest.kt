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

    @Test fun testGrammar() {
        val n = 100
        val input = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)

        val a = terminal("a")
        val b = terminal("b")
        val c = terminal("c")

        //Grammar for {a^n b^n c^n}
        val pA = fix { A: Recognizer<Int> -> transp(a) { 1 } / transp(seq(a, A)) { p -> 1 + p.second } }
        val pB = fix { B: Recognizer<Int> -> transp(seq(seq(b, B), c)) { p -> 1 + p.first.second } /
                transp(seq(b, c)) { 1 } }
        val pC = fix { C: Recognizer<Int> -> transp(seq(c, C)) { p -> 1 + p.second } / transp(c) { 1 } }
        val pD = fix { D: Recognizer<Int> -> transp(seq(seq(a, D), b)) { p -> 1 + p.first.second } /
                transp(seq(a, b)) { 1 } }
        val p = and(seq(pA, pB), seq(pD, pC))

        val grParser = transp(p, {
            if (it.first != it.second) { throw Exception("Not equal number of symbols!") }
            it.first
        })
        val res = grParser.parse(input, grParser)?.first

        Assert.assertEquals(n, res)
    }
}