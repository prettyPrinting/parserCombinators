package net.podkopaev.grammar

import net.podkopaev.grammar.Example1.*

import org.junit.Assert
import org.junit.Test

class G1Test {
    // {a^n b^n c^n | n = 1}
    @Test fun test0() {
        val p = grParser
        val result = p.get("abc")
        Assert.assertEquals("(c, c)", result.toString())
    }
    // {a^n b^n c^n | n = 8}
    @Test fun test5() {
        val p = grParser
        val result = p.get("aaaaaaaabbbbbbbbcccccccc")
        Assert.assertEquals("(c, c)", result.toString())
    }
    // {a^k | k = 8}
    @Test fun test1() {
        val p = pA
        val result = p.get("aaaaaaaa")
        Assert.assertEquals("a", result.toString())
    }
    // {c^k | k = 10}
    @Test fun test2() {
        val p = pC
        val result = p.get("cccccccccc")
        Assert.assertEquals("c", result.toString())
    }
    // {a^n b^n | n = 5}
    @Test fun test3() {
        val p = pD
        val result = p.get("aaaaabbbbb")
        Assert.assertEquals("b", result.toString())
    }
    // {b^n c^n | n = 9}
    @Test fun test4() {
        val p = pB
        val result = p.get("bbbbbbbbbccccccccc")
        Assert.assertEquals("c", result.toString())
    }
    // case {a^i b^j}
    @Test fun test9() {
        val p = grParser
        val result = p.get("aaaaabbbbbb")
        Assert.assertNotEquals("b", result.toString())
    }
    // case {b^i c^j}
    @Test fun test10() {
        val p = grParser
        val result = p.get("bbcccc")
        Assert.assertNotEquals("c", result.toString())
    }
    // case {a^i b^j c^i}
    @Test fun test6() {
        val p = grParser
        val result = p.get("aaaabbcccc")
        Assert.assertNotEquals("c", result.toString())
    }
    // case {a^i b^j c^j}
    @Test fun test7() {
        val p = grParser
        val result = p.get("abbbccc")
        Assert.assertNotEquals("c", result.toString())
    }
    // case {a^i b^i c^j}
    @Test fun test8() {
        val p = grParser
        val result = p.get("aaaaabbbbbcccc")
        Assert.assertNotEquals("c", result.toString())
    }
}