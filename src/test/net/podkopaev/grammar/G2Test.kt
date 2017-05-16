package net.podkopaev.grammar.Example2

import java.util.*
import org.junit.Assert
import org.junit.Test

class G2Test {
    // {w c w | |w| = n = 1, w in {a, b}*}
    @Test fun test0() {
        generateTest(1)
    }
    // {w c w | |w| = n = 5, w in {a, b}*}
    @Test fun test1() {
        generateTest(5)
    }
    // {w c w | |w| = n = 150, w in {a, b}*}
    @Test fun test2() {
        generateTest(150)
    }
    // {w c w | |w| = n = 87, w in {a, b}*}
    @Test fun test3() {
        generateTest(87)
    }
    // {w c w | |w| = n = 111, w in {a, b}*}
    @Test fun test4() {
        generateTest(111)
    }
    // {w c w | |w| = n = 207, w in {a, b}*}
    @Test fun test5() {
        generateTest(207)
    }
    // {w c w | |w| = n = 250, w in {a, b}*}
    @Test fun test6() {
        generateTest(250)
    }
    // {w c w | |w| = n = 177, w in {a, b}*}
    @Test fun test7() {
        assert(performanceTest(177) < 4.0)
    }
    // {w c w | |w| = n = 300, w in {a, b}*}
    @Test fun test8() {
        assert(performanceTest(300) < 5.0)
    }
    // {w c w | |w| = n = 120, w in {a, b}*}
    @Test fun test9() {
        assert(performanceTest(120) < 1.0)
    }
    // {w c w | |w| = n = 139, w in {a, b}*}
    @Test fun test10() {
        assert(performanceTest(139) < 3.0)
    }
    // {w c w | |w| = n = 107, w in {a, b}*}
    @Test fun test11() {
        assert(performanceTest(107) < 1.0)
    }
    // {w c w | |w| = n = 177, w in {a, b}*}
    @Test fun test12() {
        assert(performanceTest(177) < 3.0)
    }
    // {w c w | |w| = n = 210, w in {a, b}*}
    @Test fun test13() {
        assert(performanceTest(210) < 3.0)
    }

    fun generateTest(n: Int ) {
        val p = grParser
        val str = getRandomString(n)
        Assert.assertEquals(n, p.parse(str, p))
    }
    fun getRandomString(n: Int): String {
        val r = Random()
        val sb = StringBuilder()
        var cnt = n
        while (cnt > 0) {
            if (r.nextBoolean()) sb.append("a") else sb.append("b")
            cnt--
        }
        return(sb.toString() + "c" + sb.toString())
    }
    fun performanceTest(n: Int): Double {
        val p = grParser
        val str = getRandomString(n)
        val startTime = System.nanoTime()
        val res = p.parse(str, p)
        val endTime = System.nanoTime()
        Assert.assertEquals(n, res)
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
        return finalTime
    }
}