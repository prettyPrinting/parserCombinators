package net.podkopaev.grammar.Example1

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
    // {a^n b^n c^n | n = 217}
    @Test fun test5() {
        generateTest(217)
    }
    // {a^n b^n c^n | n = 300}
    @Test fun test6() {
        generateTest(300)
    }
    // {a^n b^n c^n | n = 29}
    @Test fun test7() {
        assert(performanceTest(29) < 1.0)
    }
    // {a^n b^n c^n | n = 99}
    @Test fun test8() {
        assert(performanceTest(99) < 1.0)
    }
    // {a^n b^n c^n | n = 50}
    @Test fun test9() {
        assert(performanceTest(50) < 1.0)
    }
    // {a^n b^n c^n | n = 190}
    @Test fun test10() {
        assert(performanceTest(190) < 5.0)
    }
    // {a^n b^n c^n | n = 100}
    @Test fun test11() {
        assert(performanceTest(100) < 2.0)
    }
    fun generateTest(n: Int) {
        val p = grParser
        val str = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)
        Assert.assertEquals(n, p.parse(str, p))
    }
    fun performanceTest(n: Int): Double {
        val p = grParser
        val str = "a".repeat(n) + "b".repeat(n) + "c".repeat(n)
        val startTime = System.nanoTime()
        val res = p.parse(str, p)
        val endTime = System.nanoTime()
        Assert.assertEquals(n, res)
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
        return finalTime
    }
}