package net.podkopaev.cpsComb

import org.junit.Assert
import org.junit.Test

class CpsCombTest: Recognizers<Int>() {
    override fun invoke(k: K<Int>) { }

    @Test fun test1() {
        val input = "x"
        val p = terminal("x")
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
    @Test fun test2() {
        val input = "abcd"
        val p = seq(seq(terminal("a"), terminal("b")),
                                seq(terminal("c"), terminal("d")))
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
    @Test fun test6() {
        val input = "uuuuq"
        val p = fix { e: Recognizer<Int> -> seq(terminal("u"), e) /
                                                   terminal("q") }
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }

     @Test fun test3() {
         val input = "ar"
         val p = seq(terminal("a"), terminal("l")) /
                 seq(terminal("a"), terminal("r"))
         val result = parse(input, p)
         Assert.assertEquals("success", result)
     }
     @Test fun test4() {
         val input = "++++x"
         val p = fix { e: Recognizer<Int> -> terminal("x") / seq(terminal("+"), e) }
         val result = parse(input, p)
         Assert.assertEquals("success", result)
     }
    @Test fun test7() {
        val input = "aaaaaaaaaa"
        val p = fix { e: Recognizer<Int> -> terminal("a") / seq(terminal("a"), e) }
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
     @Test fun test5() {
         val input = "bbbbb"
         // Left recursive rule.
         val p = fix { s: Recognizer<Int> -> terminal("b") / seq(s, terminal("b")) }
         val result = parse(input, p)
         Assert.assertEquals("success", result)
     }
    @Test fun test8() {
        val input = "bbbbbcb"
        // Left recursive rule.
        val p = fix { s: Recognizer<Int> -> terminal("b") / seq(s, terminal("b")) }
        val result = parse(input, p)
        Assert.assertEquals("fail", result)
    }
    @Test fun test9() {
        val input = "bbbbbb"
        // Left recursive rule.
        val p = fix { s: Recognizer<Int> -> terminal("b") / seq(s, s) }
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
    @Test fun test10() {
        val n = 100
        val input = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)

        val a = terminal("a")
        val b = terminal("b")
        val c = terminal("c")

        //Grammar for {a^n b^n c^n}
        val pA = fix { A: Recognizer<Int> -> seq(a, A) / a }
        val pB = fix { B: Recognizer<Int> -> seq(seq(b, B), c) / seq(b, c) }
        val pC = fix { C: Recognizer<Int> -> seq(c, C) / c }
        val pD = fix { D: Recognizer<Int> -> seq(seq(a, D), b) / seq(a, b) }
        val p = and(seq(pA, pB), seq(pD, pC))

        val res = parse(input, p)
        Assert.assertEquals("success", res)
    }
    @Test fun test11() {
        val n = 100
        val input = "a".repeat(n) + "b".repeat(n) + "c".repeat(n+1)

        val a = terminal("a")
        val b = terminal("b")
        val c = terminal("c")

        //Grammar for {a^n b^n c^n}
        val pA = fix { A: Recognizer<Int> -> seq(a, A) / a }
        val pB = fix { B: Recognizer<Int> -> seq(seq(b, B), c) / seq(b, c) }
        val pC = fix { C: Recognizer<Int> -> seq(c, C) / c }
        val pD = fix { D: Recognizer<Int> -> seq(seq(a, D), b) / seq(a, b) }
        val p = and(seq(pA, pB), seq(pD, pC))

        val res = parse(input, p)
        Assert.assertEquals("fail", res)
    }

    fun parse(s: String, p: Recognizer<Int>): String {
        init(s)
        var result = ""
        val k0: K<Int> = { x -> if (x == s.length) result = "success"
                                else result = "fail" }
        p(0)(k0)
        Trampoline.run()
        return result
    }
}