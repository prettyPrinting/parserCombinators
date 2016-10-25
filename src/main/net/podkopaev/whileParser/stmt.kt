package net.podkopaev.whileParser

import java.util.*
import net.podkopaev.booleanComb.*

sealed class Stmt() {
    companion object {
        protected var env: HashMap<String, Int> = HashMap()
        protected var input: List<Int> = listOf()
    }

    class Read(val name: String): Stmt() {
        override fun interpret_help(): List<Int> {
            if (input.isEmpty()) { throw Exception("Empty input stream error!") }
            val value = input[0]
            input = input.drop(1)
            env.put(name, value)
            return listOf()
        }
    }
    class Write(val expr: Expr): Stmt() {
        override fun interpret_help(): List<Int> {
            val value = expr.calc(env)
            return listOf(value)
        }
    }
    class Assign(val name: String, val expr: Expr): Stmt() {
        override fun interpret_help(): List<Int> {
            val value = expr.calc(env)
            env.put(name, value)
            return listOf()
        }
    }
    class Seq(val left: Stmt, val right: Stmt): Stmt() {
        override fun interpret_help(): List<Int> {
            val lresult = left.interpret_help()
            val rresult = right.interpret_help()
            return lresult + rresult
        }
    }
    class If(val expr: Expr, val then: Stmt, val elsep: Stmt): Stmt() {
        override fun interpret_help(): List<Int> {
            val value = expr.calc(env)
            if (value != 0) {
                return then.interpret_help()
            }
            return elsep.interpret_help()
        }
    }
    class While(val expr: Expr, val body: Stmt): Stmt() {
        override fun interpret_help(): List<Int> {
            val value = expr.calc(env)
            if (value == 0) { return listOf() }
            return body.interpret_help() + interpret_help()
        }
    }

    fun interpret(input: List<Int>): List<Int> {
        Companion.input = input
        env = HashMap()
        return interpret_help()
    }

    abstract protected fun interpret_help(): List<Int>
}

fun generateStmtParser(): Parser<Stmt> {
    val readp   = sp(litp("read" )) seqr paren(sp(symbol))     map { Stmt.Read (it) as Stmt }
    val writep  = sp(litp("write")) seqr paren(sp(exprParser)) map { Stmt.Write(it) as Stmt }
    val assignp = (sp(symbol) seql sp(litp(":="))) + sp(exprParser) map { nameexpr ->
        Stmt.Assign(nameexpr.first, nameexpr.second) as Stmt
    }

    val parserProxy = proxy<Stmt>()
    val ifp =
        (sp(litp("if"  )) seqr sp(exprParser )) +
        (sp(litp("then")) seqr sp(parserProxy)) +
        (sp(litp("else")) seqr sp(parserProxy)) - litp("fi") map {
            ete -> Stmt.If(ete.first.first, ete.first.second, ete.second) as Stmt
        }
    val whilep =
            (sp(litp("while")) seqr  sp(exprParser )) +
            (sp(litp("do"   )) seqr  sp(parserProxy)) -
            litp("od") map {
                eb -> Stmt.While(eb.first, eb.second) as Stmt
            }

    val corep = sp(readp / writep / assignp / ifp / whilep)
    val parser = leftAssocp(sp(litp(";")), corep) {
        op, s1, s2 ->
        Stmt.Seq(s1, s2)
    }
    parserProxy.parser = parser
    return parser
}
val stmtParser: Parser<Stmt> = generateStmtParser()
