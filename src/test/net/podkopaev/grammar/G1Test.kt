package net.podkopaev.grammar

import net.podkopaev.grammar.Example1.*

import org.junit.Assert
import org.junit.Test

class G1Test {
    // {a^n b^n c^n | n = 1}
    @Test fun test1() {
        generateTest(1)
    }
    // {a^n b^n c^n | n = 9}
    @Test fun test2() {
        generateTest(9)
    }
    // {a^n b^n c^n | n = 300}
    @Test fun test3() {
        generateTest(300)
    }
    // {a^n b^n c^n | n = 500}
    @Test fun test4() {
        performanceTest(500)
    }
    fun generateTest(n: Int) {
        val p = grParser
        val str = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)
        Assert.assertEquals(n, p.get(str))
    }
    // result: 0.464000025 sec.
    fun performanceTest(n: Int) {
        val startTime = System.nanoTime()
        generateTest(n)
        val endTime = System.nanoTime()
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
    }
}