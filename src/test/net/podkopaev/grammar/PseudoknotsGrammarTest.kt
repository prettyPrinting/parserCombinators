package net.podkopaev.grammar.PseudoknotsGrammar

import org.junit.Assert
import org.junit.Test

class PseudoknotsGrammarTest {
    @Test fun test0() {
        val inp = "[(])"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test1() {
        val inp = "[[((]]))"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test2() {
        val inp = "[((]))"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test8() {
        val inp = "[[((((]]))))"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test4() {
        val inp = "[".repeat(50) + "(".repeat(100) + "]".repeat(50) + ")".repeat(100)
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test5() {
        val inp = "[(((((])))))"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test6() {
        val inp = "[[[[[[[(]]]]]]])"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
    @Test fun test7() {
        val inp = "[[[(((]]])))"
        val p = grParser
        Assert.assertEquals(inp.length, p.parse(inp, p))
    }
}