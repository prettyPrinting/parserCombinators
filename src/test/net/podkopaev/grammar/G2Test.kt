package net.podkopaev.grammar

import net.podkopaev.grammar.Example2.*
import java.util.*
import org.junit.Assert
import org.junit.Test

class G2Test {
    // {w c w | |w| = n = 1}
    @Test fun test0() {
        generateTest(1)
    }
    // {w c w | |w| = n = 5}
    @Test fun test1() {
        generateTest(5)
    }
    // {w c w | |w| = n = 150}
    @Test fun test2() {
        generateTest(150)
    }
    // {w c w | |w| = n = 87}
    @Test fun test3() {
        generateTest(87)
    }
    // {w c w | |w| = n = 111}
    @Test fun test4() {
        generateTest(111)
    }
    // {w c w | |w| = n = 207}
    @Test fun test5() {
        generateTest(207)
    }
    // {w c w | |w| = n = 250}
    @Test fun test6() {
        generateTest(250)
    }
    // {w c w | |w| = n = 177}
    @Test fun test7() {
        assert(performanceTest(177) < 1.0)
    }
    // {w c w | |w| = n = 300}
    @Test fun test8() {
        assert(performanceTest(300) < 1.0)
    }
    // {w c w | |w| = n = 120}
    @Test fun test9() {
        assert(performanceTest(120) < 1.0)
    }
    // {w c w | |w| = n = 139}
    @Test fun test10() {
        assert(performanceTest(139) < 1.0)
    }
    // {w c w | |w| = n = 107}
    @Test fun test11() {
        assert(performanceTest(107) < 1.0)
    }
    // {w c w | |w| = n = 177}
    @Test fun test12() {
        assert(performanceTest(177) < 1.0)
    }
    // {w c w | |w| = n = 210}
    @Test fun test13() {
        assert(performanceTest(210) < 1.0)
    }
    fun generateTest(n: Int ) {
        val p = grParser
        val str = getRandomString(n)
        Assert.assertEquals(n, p.get(str))
    }
    fun getRandomString(n: Int): String {
        val r = Random()
        val sb = StringBuilder()
        for (j in 0..n - 1) {
            if (r.nextBoolean()) {
                sb.append("a")
            } else {
                sb.append("b")
            }
        }
        return(sb.toString() + "c" + sb.toString())
    }
    fun performanceTest(n: Int): Double {
        val startTime = System.nanoTime()
        generateTest(n)
        val endTime = System.nanoTime()
        val finalTime = (endTime - startTime) /
                Math.pow(10.toDouble(), 9.toDouble())
        print("Length: " + (n * 2 + 1) + '\n'+ "Time: " + finalTime + '\n')
        return finalTime
    }
}