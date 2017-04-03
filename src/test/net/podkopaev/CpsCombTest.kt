package net.podkopaev.cpsResult

import org.junit.Assert
import org.junit.Test

class CpsCombTest: Recognizers<Int>() {
    override fun invoke(k: K<Int>) { }

    @Test fun test1() {
        val input = "x"
        val p: Recognizer = terminal("x")
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
    @Test fun test2() {
        val input = "abcd"
        val p: Recognizer = seq(seq(terminal("a"), terminal("b")),
                                seq(terminal("c"), terminal("d")))
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
    @Test fun test6() {
        val input = "uuuuq"
        val p: Recognizer = fix { e: Recognizer -> seq(terminal("u"), e) /
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
         val p = fix { e: Recognizer -> terminal("x") / seq(terminal("+"), e) }
         val result = parse(input, p)
         Assert.assertEquals("success", result)
     }
    @Test fun test7() {
        val input = "aaaaaaaaaa"
        val p = fix { e: Recognizer -> terminal("a") / seq(terminal("a"), e) }
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }
     @Test fun test5() {
         val input = "bbbbb"
         // Left recursive rule.
         val p = fix ({ s -> terminal("b") / seq(s, terminal("b")) })
         val result = parse(input, p)
         Assert.assertEquals("success", result)
     }
    @Test fun test8() {
        val input = "bbbbbcb"
        // Left recursive rule.
        val p = fix ({ s -> terminal("b") / seq(s, terminal("b")) })
        val result = parse(input, p)
        Assert.assertEquals("fail", result)
    }
    @Test fun test9() {
        val input = "bbbbbb"
        // Left recursive rule.
        val p = fix ({ s -> terminal("b") / seq(s, s) })
        val result = parse(input, p)
        Assert.assertEquals("success", result)
    }

    fun parse(s: String, p: Recognizer): String {
        init(s)
        var result = ""
        val k0: K<Int> = { x -> if (x == s.length) result = "success"
                                else result = "fail" }
        p(0)(k0)
        Trampoline.run()
        return result
    }
}