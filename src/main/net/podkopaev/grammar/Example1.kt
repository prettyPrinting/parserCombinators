package net.podkopaev.grammar

import java.util.*
import net.podkopaev.booleanComb.*
/*
Conjunctive grammar for language {a^n b^n c^n}
 */

fun rParser() = (
        conjp(seqrp(A(), B()), seqrp(D(), C()))
        )

val epsilon: Parser<Char> = char('\u0000')
val a = char('a')
val b = char('b')
val c = char('c')

fun B() : Parser<Char> = seqrp(b, B1()) / epsilon
fun B1(): Parser<Char> = seqrp(B(), c)
fun D() : Parser<Char> = seqrp(a, D1()) / epsilon
fun D1(): Parser<Char> = seqrp(D(), b)

fun  A(): Parser<Char> = fix {
    val r = seqrp(a, A()) / epsilon
    return@fix r
}

fun C(): Parser<Char> = fix {
    val r = seqrp(c, C()) / epsilon
    return@fix r
}


