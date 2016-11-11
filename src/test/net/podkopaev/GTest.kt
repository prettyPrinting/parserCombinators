package net.podkopaev

import net.podkopaev.grammar.*
import net.podkopaev.booleanComb.*

import org.junit.Assert
import org.junit.Test

class GTest {
    @Test fun test0() {
        val p = grParser
        val result = p.get("aaakbbbkccc")
        Assert.assertEquals("c", result.toString())
    }
}