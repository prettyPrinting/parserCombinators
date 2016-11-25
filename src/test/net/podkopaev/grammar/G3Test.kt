package net.podkopaev.grammar.Example3

import org.junit.Assert
import org.junit.Test
import java.util.*

class G3Test {
    @Test fun test1() {
        generateTest(10)
    }
    @Test fun test2() {
        generateTest(29)
    }
    @Test fun test3() {
        generateTest(79)
    }
    @Test fun test4() {
        generateTest(120)
    }
    @Test fun test5() {
        generateTest(181)
    }
    @Test fun test6() {
        generateTest(204)
    }
    fun generateTest(n: Int ) {
        val p = grParser
        val str = getRandomString(n)
        Assert.assertEquals(n * 2, p.get(str))
    }
    fun getRandomString(n: Int): String {
        val r = Random()
        val sb = StringBuilder()
        var cnt = n
        while(cnt > 0) {
            if (r.nextBoolean()) {
                sb.append("a")
            } else {
                sb.append("b")
            }
            cnt--
        }
        return(sb.toString() + sb.toString())
    }
}