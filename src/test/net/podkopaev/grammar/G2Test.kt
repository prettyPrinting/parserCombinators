package net.podkopaev.grammar

import net.podkopaev.grammar.Example2.*

import org.junit.Assert
import org.junit.Test

class G2Test {
    // {lcl | l = (a | b)*}
    @Test fun test0() {
        val p = grParser
        val result = p.get("abaabbacabaabba")
        Assert.assertEquals("(a, a)", result.toString())
    }
    // {b^n c b^n | n = 5}
    @Test fun test1() {
        val p = grParser
        val result = p.get("bbbbbcbbbbb")
        Assert.assertEquals("(b, b)", result.toString())
    }
    // {a^n c a^n | n = 10}
    @Test fun test2() {
        val p = grParser
        val result = p.get("aaaaaaaaaacaaaaaaaaaa")
        Assert.assertEquals("(a, a)", result.toString())
    }
    // {lcw | l != w}
    @Test fun test3() {
        val p = grParser
        val result = p.get("abacbab")
        Assert.assertNotEquals("(b, b)", result.toString())
    }
}