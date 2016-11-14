package net.podkopaev.grammar.Example1

import net.podkopaev.booleanComb.*
/*
Conjunctive grammar for language {a^n b^n c^n}
S  -> AB & DC           {a^i b^j c^k | j = k} &
                        {a^i b^j c^k | i = j}
A  -> aA  | eps     {a*}
B  -> bBc | eps     {b^n c^n}

D  -> aDb | eps     {a^k b^k}
C  -> cC  | eps     {c*}
 */

val a = char('a') map { 1 }
val b = char('b') map { 1 }
val c = char('c') map { 1 }
val eps = conp('e') map { 0 }

val pA: Parser<Int> = fix { A -> eps / a / ((a seqr A seql a) map { it + 2 }) }
val pB: Parser<Int> = fix { B -> eps     / ((b seqr B seql c) map { it + 1 }) }

val pD: Parser<Int> = fix { D -> eps     / ((a seqr D seql b) map { it + 1 }) }
val pC: Parser<Int> = fix { C -> eps / c / ((c seqr C seql c) map { it + 2 }) }

fun createGrParser(): Parser<Int> {
    return conjp(pA seqr pB, pD seqr pC) map {
        if (it.first != it.second) { throw Exception("Not equal number of symbols!") }
        it.first
    }
}
val grParser: Parser<Int> = createGrParser()