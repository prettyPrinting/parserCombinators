package net.podkopaev.grammar.HairpinGrammar

import org.junit.Assert
import org.junit.Test

class HairpinGrammarTest {
    @Test fun test1() {
        val p = pS
        val inp = "aaagaaauuu"
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test2() {
        val p = pS
        val inp = "cacgaaagug"
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test3() {
        val p = pS
        val inp = "guagcaauac"
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test4() {
        val p = pS
        val inp = "uuugcaaaaa"
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test5() {
        val p = pS
        val inp = "aucgaaagau"
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
}
