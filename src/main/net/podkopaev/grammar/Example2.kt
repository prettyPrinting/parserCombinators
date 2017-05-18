package net.podkopaev.grammar.Example2

import net.podkopaev.cpsComb.*
 /*
 Conjunctive grammar for language { wcw | w in {a, b}* }
 S -> C & D
 C -> XCX | c
 D -> aA & aD | bB & bD | cE
 A -> XAX | cEa
 B -> XBX | cEb
 E -> XE  | eps
 X -> a   | b
 */

val a = terminal("a") map { 1 }
val b = terminal("b") map { 1 }
val c = terminal("c") map { -1 }
val e = eps map { 0 }

val pX: Recognizer<Int> = a / b
val pC: Recognizer<Int> = fix { C -> c   / (pX seqr C seql pX) map { it + 1 } }
val pE: Recognizer<Int> = fix { E -> e / pX / (pX seqr E seql pX) }

val pA: Recognizer<Int> = fix { A -> (pX seqr A seql pX) / (c seqr pE seql a) }
val pB: Recognizer<Int> = fix { B -> (pX seqr B seql pX) / (c seqr pE seql b) }

val pD: Recognizer<Int> = fix { D -> (and(a seqr pA, a seqr D) map { 0 }) /
        (and(b seqr pB, b seqr D) map { 0 }) / (c seqr pE)
}
fun createGrParser(): Recognizer<Int> {
    return and(pC, pD) map { it.first }
}
val grParser: Recognizer<Int> = createGrParser()