package net.podkopaev.grammar.Example2

import net.podkopaev.booleanComb.*
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

val a = char('a') map { 1 }
val b = char('b') map { 1 }
val c = char('c') map { -1 }
val eps = conp('e') map { 0 }

val pX: Parser<Int> = a / b
val pC: Parser<Int> = fix { C -> c   / (pX seqr C seql pX) map { it + 1 } }
val pE: Parser<Int> = fix { E -> eps / pX / (pX seqr E seql pX) }

val pA: Parser<Int> = fix { A -> (pX seqr A seql pX) / (c seqr pE seql a) }
val pB: Parser<Int> = fix { B -> (pX seqr B seql pX) / (c seqr pE seql b) }

val pD: Parser<Int> = fix { D -> (conjp(a seqr pA, a seqr D) map { 0 }) /
        (conjp(b seqr pB, b seqr D) map { 0 }) / (c seqr pE)
}
fun createGrParser(): Parser<Int> {
    return conjp(pC, pD) map { it.first }
}
val grParser: Parser<Int> = createGrParser()