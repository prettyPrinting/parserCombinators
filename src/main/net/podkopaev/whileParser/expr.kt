package net.podkopaev.whileParser

import net.podkopaev.booleanComb.*

sealed class Expr() {
    class Con(val value: Int): Expr() {
        override fun calc(env: Map<String, Int>): Int = value
    }
    class Var(val name: String): Expr() {
        override fun calc(env: Map<String, Int>): Int = env.getOrElse(name, { 0 })
    }
    class Binop(val op: String, val left: Expr, val right: Expr): Expr() {
        override fun calc(env: Map<String, Int>): Int {
            val l = left.calc(env)
            val r = right.calc(env)
            return when (op) {
                "+" -> l + r
                "-" -> l - r
                "*" -> l * r
                "/" -> l / r
                "%" -> l % r
                "^" -> Math.pow(l.toDouble(), r.toDouble()).toInt()
                else -> throw UnsupportedOperationException()
            }
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is Con -> value.hashCode()
            is Var -> name.hashCode()
            is Binop -> (op.hashCode() * 31 + left.hashCode()) * 31 + right.hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Expr) { return false }
        when (other) {
            is Con -> return (this is Con) && value.equals(other.value)
            is Var -> return (this is Var) && name.equals(other.name)
            is Binop -> return (this is Binop) && op.equals(other.op) &&
                    left.equals(other.left) && right.equals(other.right)
        }
        return false
    }

    abstract fun calc(env: Map<String, Int>): Int
}

fun generateExprParser(): Parser<Expr> = fix {
    val corep =
            (number map { Expr.Con(it) as Expr }) /
            (symbol map { Expr.Var(it) as Expr }) /
            paren( sp(it) )
    val op1p = rightAssocp(sp(litp("^")), corep) { l, op, r -> Expr.Binop(l, op, r) }
    val op2p = leftAssocp (sp(litp("*") / litp("/") / litp("%")), op1p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
    val op3p = leftAssocp (sp(litp("+") / litp("-")), op2p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
    return@fix op3p
}

val exprParser: Parser<Expr> = generateExprParser()
