package net.podkopaev

import net.podkopaev.whileParser.Expr
import net.podkopaev.whileParser.exprParser
import net.podkopaev.whileParser.stmtParser
import org.junit.Assert
import org.junit.Test

class LTest {
    @Test fun test0() {
        val parser = exprParser
        Assert.assertEquals(Expr.Con(123), parser.get("123"))
    }

    @Test fun test01() {
        val parser = exprParser
        Assert.assertEquals(Expr.Con(123), parser.get(" ( 123 ) "))
    }
    @Test fun test1() {
        val parser = exprParser
        val result = parser.get("  x1z ")
        Assert.assertEquals(Expr.Var("x1z"), result)
    }

    /*
    @Test fun test2() {
        val parser = exprParser
        val result = parser.get("1 ^ 2 ^ 3")
        Assert.assertEquals(Expr.Binop("^", Expr.Con(1),
                Expr.Binop("^", Expr.Con(2), Expr.Con(3))),
                result)
    }
    */

    @Test fun test03() {
        val parser = exprParser
        val result = parser.get("1+2")
        Assert.assertEquals(Expr.Binop("+", Expr.Con(1), Expr.Con(2)),
                result)
    }

    @Test fun test3() {
        val parser = exprParser
        val result = parser.get("1 + (2 + 3)")
        Assert.assertEquals(Expr.Binop("+", Expr.Con(1),
                Expr.Binop("+", Expr.Con(2), Expr.Con(3))),
                result)
    }

    @Test fun test4() {
        val parser = exprParser
        val result = parser.get("1 + 2 + 3")
        Assert.assertEquals(Expr.Binop("+", Expr.Binop("+", Expr.Con(1), Expr.Con(2)),
                Expr.Con(3)),
                result)
    }

    @Test fun test5() {
        val parser = exprParser
        val result = parser.get("1 + 2 * 3")
        Assert.assertEquals(7, result?.calc(hashMapOf()))
    }

    @Test fun test6() {
        val parser = exprParser
        val result = parser.get("8 - 2 * (15 / 2)")
        Assert.assertEquals(-6, result?.calc(hashMapOf()))
    }

    @Test fun test7() {
        val parser = stmtParser
        val result = parser.get("write(5)")
        Assert.assertEquals(listOf(5), result?.interpret(listOf()))
    }

    @Test fun test8() {
        val parser = stmtParser
        val result = parser.get("x := 8 - 2 * (15 / 2); write(x)")
        Assert.assertEquals(listOf(-6), result?.interpret(listOf()))
    }

    @Test fun test9() {
        val parser = stmtParser
        val result = parser.get("write(0-5);write(5)")
        Assert.assertEquals(listOf(-5,5), result?.interpret(listOf()))
    }

    @Test fun test10() {
        val parser = stmtParser
        val result = parser.get("x:=3;write(0-5);write(5)")
        Assert.assertEquals(listOf(-5,5), result?.interpret(listOf()))
    }

    @Test fun test11() {
        val parser = stmtParser
        val result = parser.get("x := 3")
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf()))
    }

    @Test fun test12() {
        val program = """
read(n);
read(k);
r := 1
        """
        val result = stmtParser.get(program)
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf(2, 5)))
    }

    @Test fun test13() {
        val program = """
read(n);
read(k);
r := 1;
r := 2
        """
        val result = stmtParser.get(program)
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf(2, 5)))
    }

    val logpowProgram = """
read(n);
read(k);
r := 1;
while k do
  if (k % 2) then
    r := r * n;
    k := k - 1
  else
    n := n * n;
    k := k / 2
  fi
od;
write(r)
        """

    @Test fun test14() {
        val result = stmtParser.get(logpowProgram)
        Assert.assertEquals(listOf(32), result?.interpret(listOf(2, 5)))
    }
}
