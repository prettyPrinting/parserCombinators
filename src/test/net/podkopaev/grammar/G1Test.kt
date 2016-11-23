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
    // {a^n b^n c^n | n = 91}
    @Test fun test3() {
        generateTest(91)
    }
    // {a^n b^n c^n | n = 111}
    @Test fun test4() {
        generateTest(111)
    }
    // {a^n b^n c^n | n = 290}
    @Test fun test5() {
        assert(performanceTest(290) < 1.0)
    }
    // {a^n b^n c^n | n = 140}
    @Test fun test6() {
        assert(performanceTest(140) < 1.0)
    }
    // {a^n b^n c^n | n = 170}
    @Test fun test7() {
        assert(performanceTest(170) < 1.0)
    }
    // {a^n b^n c^n | n = 300}
    @Test fun test8() {
        assert(performanceTest(300) < 1.0)
    }
    // {a^n b^n c^n | n = 99}
    @Test fun test9() {
        assert(performanceTest(99) < 1.0)
    }
    // {a^n b^n c^n | n = 156}
    @Test fun test10() {
        assert(performanceTest(156) < 1.0)
    }
    // {a^n b^n c^n | n = 199}
    @Test fun test11() {
        assert(performanceTest(199) < 1.0)
    }
    // {a^n b^n c^n | n = 399}
    @Test fun test12() {
        assert(performanceTest(399) < 1.0)
    }
    // {a^n b^n c^n | n = 300}
    @Test fun test13() {
        assert(performanceTest(300) < 1.0)
    }

    fun generateTest(n: Int) {
        val p = grParser
        val str = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)
        Assert.assertEquals(n, p.get(str))
    }

    fun performanceTest(n: Int): Double {
        val startTime = System.nanoTime()
        generateTest(n)
        val endTime = System.nanoTime()
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
        print("Length: " + n * 3 + '\n'+ "Time: " + finalTime + '\n')
        return finalTime
    }
}