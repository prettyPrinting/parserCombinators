package net.podkopaev.grammar.Example3

import net.podkopaev.cpsComb.*
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
val e = eps map { 0 }

val pX: Recognizer<Int> = a / b
val pA: Recognizer<Int> = fix { A -> a / ((pX seqr A seql pX) map { it + 2 }) }
val pB: Recognizer<Int> = fix { B -> b / ((pX seqr B seql pX) map { it + 2 }) }

val pR: Recognizer<Int> = pX seqr pX map { it + 1 }
val pC: Recognizer<Int> = fix { C -> e / pR / ((pR seqr C seql pR) map { it + 4 }) }

fun createGrParser(): Recognizer<Int> {
    return and(andNot(pC, pA seqr pB), andNot(pC, pB seqr pA)) map { it.first }
}
val grParser: Recognizer<Int> = createGrParser()