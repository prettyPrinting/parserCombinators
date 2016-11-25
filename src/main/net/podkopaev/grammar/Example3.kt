package net.podkopaev.grammar.Example3

import net.podkopaev.booleanComb.*
/*
Boolean grammar for language {ww | w in {a, b}*}
S -> ~AB & ~BA & C
A -> XAX | a
B -> XBX | b
C -> XXC | eps
X -> a   | b
 */

val a = char('a') map { 1 }
val b = char('b') map { 1 }
val eps = conp('e') map { 0 }

val pX: Parser<Int> = a / b
val pA: Parser<Int> = fix { A -> a / ((pX seqr A seql pX) map { it + 2 }) }
val pB: Parser<Int> = fix { B -> b / ((pX seqr B seql pX) map { it + 2 }) }

val pR: Parser<Int> = pX seqr pX map { it + 1 }
val pC: Parser<Int> = fix { C -> eps / pR / ((pR seqr C seql pR) map { it + 4 }) }

fun createGrParser(): Parser<Int> {
    return conjp(conjNotp(pC, pA seqr pB), conjNotp(pC, pB seqr pA)) map { it.first }
}
val grParser: Parser<Int> = createGrParser()