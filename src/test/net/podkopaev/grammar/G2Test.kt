package net.podkopaev.grammar

import net.podkopaev.grammar.Example2.*

import org.junit.Assert
import org.junit.Test

class G2Test {
    @Test fun test0() {
        val p = pA
        val result = p.get("ackaa")
        Assert.assertEquals("a", result.toString())
    }
}