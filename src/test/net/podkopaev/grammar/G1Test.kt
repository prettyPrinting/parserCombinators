package net.podkopaev.grammar

import net.podkopaev.grammar.Example1.*

import java.io.*
import org.junit.Assert
import org.junit.Test

class G1Test {
    // {a^n b^n c^n | n = 1}
    @Test fun test0() {
        val p = grParser
        val result = p.get("abc")
        Assert.assertEquals(1, result)
    }
    // {a^n b^n c^n | n = 8}
    @Test fun test5() {
        val p = grParser
        val result = p.get("aaaaaaaabbbbbbbbcccccccc")
        Assert.assertEquals(8, result)
    }
    // {a^k | k = 8}
    @Test fun test1() {
        val p = pA
        val result = p.get("aaaaaaaa")
        Assert.assertEquals(8, result)
    }
    // {c^k | k = 10}
    @Test fun test2() {
        val p = pC
        val result = p.get("cccccccccc")
        Assert.assertEquals(10, result)
    }
    // {a^n b^n | n = 5}
    @Test fun test3() {
        val p = pD
        val result = p.get("aaaaabbbbb")
        Assert.assertEquals(5, result)
    }
    // {b^n c^n | n = 9}
    @Test fun test4() {
        val p = pB
        val result = p.get("bbbbbbbbbccccccccc")
        Assert.assertEquals(9, result)
    }
    // case {a^i b^j}
    @Test fun test9() {
        val p = grParser
        val result = p.get("aaaaabbbbbb")
        Assert.assertEquals(null, result)
    }
    // case {b^i c^j}
    @Test fun test10() {
        val p = grParser
        val result = p.get("bbcccc")
        Assert.assertEquals(null, result)
    }
    // case {a^i b^j c^i}
    @Test fun test6() {
        val p = grParser
        val result = p.get("aaaabbcccc")
        Assert.assertEquals(null, result)
    }
    // case {a^i b^j c^j}
    @Test fun test7() {
        val p = grParser
        val result = p.get("abbbccc")
        Assert.assertEquals(null, result)
    }
    // case {a^i b^i c^j}
    @Test fun test8() {
        val p = grParser
        val result = p.get("aaaaabbbbbcccc")
        Assert.assertEquals(null, result)
    }
    // {a^n b^n c^n | n = 500}
    @Test fun test11() {
        val p = grParser
        val str = File("src/test/net/podkopaev/grammar/data/E1_0.txt").readText(charset = Charsets.UTF_8)
        val result = p.get(str)
        Assert.assertEquals(500, result)
    }
}