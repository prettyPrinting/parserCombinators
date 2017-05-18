package net.podkopaev.grammar.Example3

import java.util.*
import org.junit.Assert
import org.junit.Test


class G3Test {
    // {w w | |w| = 10, w in {a, b}*}
    @Test fun test1() {
        generateTest(10)
    }
    // {w w | |w| = 29, w in {a, b}*}
    @Test fun test2() {
        generateTest(29)
    }
    // {w w | |w| = 79, w in {a, b}*}
    @Test fun test3() {
        generateTest(79)
    }
    // {w w | |w| = 120, w in {a, b}*}
    @Test fun test4() {
        generateTest(120)
    }
    // {w w | |w| = 181, w in {a, b}*}
    @Test fun test5() {
        generateTest(181)
    }
    // {w w | |w| = 304, w in {a, b}*}
    @Test fun test6() {
        generateTest(304)
    }
    // {w w | |w| = 10, w in {a, b}*}
    @Test fun test7() {
        assert(performanceTest(10) < 1.0)
    }
    // {w w | |w| = 277, w in {a, b}*}
    @Test fun test8() {
        performanceTest(277)
    }
    // {w w | |w| = 154, w in {a, b}*}
    @Test fun test9() {
        performanceTest(154) < 1.0
    }
    // {w w | |w| = 21, w in {a, b}*}
    @Test fun test10() {
        assert(performanceTest(21) < 1.0)
    }
    // {w w | |w| = 108, w in {a, b}*}
    @Test fun test11() {
        performanceTest(106) < 1.0
    }
    // {w w | |w| = 205, w in {a, b}*}
    @Test fun test12() {
        performanceTest(205) < 1.0
    }

    fun generateTest(n: Int ) {
        val p = grParser
        val str = getRandomString(n)
        Assert.assertEquals(n * 2, p.parse(str, p))
    }
    fun getRandomString(n: Int): String {
        val r = Random()
        val sb = StringBuilder()
        var cnt = n
        while(cnt > 0) {
            if (r.nextBoolean()) sb.append("a") else sb.append("b")
            cnt--
        }
        return(sb.toString() + sb.toString())
    }
    fun performanceTest(n: Int): Double {
        val p = grParser
        val str = getRandomString(n)
        val startTime = System.nanoTime()
        val res = p.parse(str, p)
        val endTime = System.nanoTime()
        Assert.assertEquals(n * 2, res)
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
        return finalTime
    }
}