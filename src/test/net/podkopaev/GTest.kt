package net.podkopaev

import net.podkopaev.grammar.*

import org.junit.Assert
import org.junit.Test

class GTest {
    @Test fun test0() {
        val parser = rParser()
        val result = parser.get("ab")
        Assert.assertEquals("ab", result.toString())
    }
}