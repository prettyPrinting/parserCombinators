package net.podkopaev.grammar

import net.podkopaev.grammar.Example1.*

import org.junit.Assert
import org.junit.Test

class G1Test {
    @Test fun test0() {
        val p = grParser
        val result = p.get("aaakbbbkccc")
        Assert.assertEquals("c", result.toString())
    }
    @Test fun test1() {
        val p = pD
        val result = p.get("aaakbbb")
        Assert.assertEquals("b", result.toString())
    }
}