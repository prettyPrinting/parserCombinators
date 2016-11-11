package net.podkopaev

import net.podkopaev.grammar.*
import net.podkopaev.booleanComb.*

import org.junit.Assert
import org.junit.Test

class GTest {
    @Test fun test0() {
        val p = grParser
        //val result = p.get("aaakbbbkccc")
        //Assert.assertEquals("c", result.toString())
    }

    @Test fun test1() {
        val m = pC
        val result = m.get("aacaa")
        Assert.assertEquals("a", result.toString())
    }
}