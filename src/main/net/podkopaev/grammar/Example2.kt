package net.podkopaev.grammar.Example2

import java.util.*
import net.podkopaev.booleanComb.*
/*
/**
 * Conjunctive grammar for language {wcw}
 */

val epsilon: Parser<Char> = char('\u0000')
val a = char('a')
val b = char('b')
val c = char('c')

fun rParser2() = (
        conjp(C(), D())
        )
fun D(): Parser<Char> = fix {
    val r = conjp(seqrp(a, A()), seqrp(a, D())) / conjp(seqrp(b, B()), seqrp(b, D())) / seqrp(c, E())
    return@fix r
}
    fun A() : Parser<Char> = seqrp(X(), X1()) / seqrp(c, E1())
    fun X1(): Parser<Char> = seqrp(A(), X())
    fun E1(): Parser<Char> = seqrp(E(), A())
    fun C() : Parser<Char> = seqrp(X(), C1()) / c
    fun C1(): Parser<Char> = seqrp(C(), X())
    fun E() : Parser<Char> = seqrp(X(), E()) / epsilon
    fun B() : Parser<Char> = seqrp(X(), B1()) / seqrp(c, E2())
    fun B1(): Parser<Char> = seqrp(B(), X())
    fun E2(): Parser<Char> = seqrp(E(), b)
    fun X() : Parser<Char> = a / b
*/