package net.podkopaev.whileParser.cpsParser

import net.podkopaev.cpsParser.*
import net.podkopaev.whileParser.Expr


import org.junit.Assert
import org.junit.Test

class LCpsParserTest {
    @Test fun test0() {
        val parser = exprParser
        Assert.assertEquals(Expr.Con(123), parser.parse("123", parser))
    }

    @Test fun test01() {
        val parser = exprParser
        Assert.assertEquals(Expr.Con(123), parser.parse("( 123 )", parser))
    }
    @Test fun test1() {
        val parser = exprParser
        val result = parser.parse("abc0", parser)
        Assert.assertEquals(Expr.Var("abc0"), result)
    }
    @Test fun test2() {
        val parser = exprParser
        val result = parser.parse("1 ^ 2 ^ 3", parser)
        Assert.assertEquals(Expr.Binop("^", Expr.Con(1),
                Expr.Binop("^", Expr.Con(2), Expr.Con(3))),
                result)
    }
    @Test fun test03() {
        val parser = exprParser
        val result = parser.parse("1 + 2", parser)
        Assert.assertEquals(Expr.Binop("+", Expr.Con(1), Expr.Con(2)),
                result)
    }

//    @Test fun test4() {
//        val parser = exprParser
//        val result = parser.parse("1 + 2 + 3", parser)
//        Assert.assertEquals(Expr.Binop("+", Expr.Binop("+", Expr.Con(1), Expr.Con(2)),
//                Expr.Con(3)),
//                result)
//    }

    @Test fun test5() {
        val parser = exprParser
        val result = parser.parse("1 + 2 * 3", parser)
        Assert.assertEquals(7, result?.calc(hashMapOf()))
    }

    @Test fun test7() {
        val parser = stmtParser
        val result = parser.parse("write ( 5 )", parser)
        Assert.assertEquals(listOf(5), result?.interpret(listOf()))
    }

    @Test fun test8() {
        val parser = stmtParser
        val result = parser.parse("x0 := 8 - 2;write ( x0 )", parser)
        Assert.assertEquals(listOf(6), result?.interpret(listOf()))
    }

    @Test fun test9() {
        val parser = stmtParser
        val result = parser.parse("write ( 5 + 8 );write ( 5 )", parser)
        Assert.assertEquals(listOf(13,5), result?.interpret(listOf()))
    }

    @Test fun test10() {
        val parser = stmtParser
        val result = parser.parse("x := 3;write ( 5 );write ( 10 )", parser)
        Assert.assertEquals(listOf(5, 10), result?.interpret(listOf()))
    }

    @Test fun test11() {
        val parser = stmtParser
        val result = parser.parse("x := 3", parser)
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf()))
    }
    @Test fun test12() {
        val program = "read ( n )"
        val parser = stmtParser
        val result = parser.parse(program, parser)
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf(2)))
    }
    @Test fun test13() {
        val program = "read ( n );read ( k )"
        val parser = stmtParser
        val result = parser.parse(program, parser)
        Assert.assertEquals(listOf<Int>(), result?.interpret(listOf(2, 5)))
    }

    @Test fun test14() {
        val parser = stmtParser
        val program = "if ( k % 2 ) then k := 0 else k := 1;write ( k ) fi"
        val result = parser.parse(program, parser)
        Assert.assertEquals(listOf(1), result?.interpret(listOf(5)))
    }
}