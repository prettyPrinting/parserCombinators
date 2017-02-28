package net.podkopaev.grammar.Example4

import net.podkopaev.booleanComb.*
/*
    WARNING: Grammar contains left recursive rule! Now it does not work.
    Conjunctive grammar for round and square embedded brackets.
    For example:
    ggggugcgacucccccgucuauccugaacgucaucaggacca
    ...............(((...[[[[[[)))...]]]]]]...

    S  -> A1A2 & A3A4
    A1 -> a | u | c | g | A5A11 |A6A12 | A7A13 | A8A14 | A6A13
    A2 -> A1A2 | a | u | c | g | A5A11 | A6A12 | A7A13 | A8A14 | A8A11 | A6A13
    A3 -> a | u | c | g | A5A3 | A6A3 | A7A3 | A8A3
    A4 -> A10A3
    A5 -> a
    A6 -> u
    A7 -> c
    A8 -> g
    A9 -> A1A2 | A5A11 | A6A12 | A7A13 | A8A14 | A8A11 | A6A13
    A10 -> A5A15 | A6A16 | A7A17 | A8A18 | A8A15 | A6A17 | A5A10 | A6A10 |
         A7A10 | A8A10 | A10A5 | A10A6 | A10A7 | A10A8 | A5A3 | A6A3 | A7A3 | A8A3
    A11 -> A9A6
    A12 -> A9A5
    A13 -> A9A8
    A14 -> A9A7
    A15 -> A10A6
    A16 -> A10A5
    A17 -> A10A8
    A18 -> A10A7
 */

val a = char('a') map { 1 }
val u = char('u') map { 1 }
val c = char('c') map { 1 }
val g = conp('g') map { 1 }
val pN  : Parser<Int> = a / u / c / g

val pA10: Parser<Int> = fix { A10 -> (a seqr A10 seql a) / (u seqr A10 seql u) / (c seqr A10 seql g) /
        (g seqr A10 seql c) / (g seqr A10 seql a) / (u seqr A10 seql g) / (a seqr A10) / (u seqr A10) /
        (c seqr A10) / (g seqr A10) / (A10 seqr a) / (A10 seqr u) / (A10 seqr c) /
        (A10 seqr g) / (a seqr pA3) / (u seqr pA3)/ (c seqr pA3) / (g seqr pA3) }

val pA9: Parser<Int> =  fix { A9 -> (pA1 seqr pA2) / (a seqr A9 seqr u)/ (u seqr pA12) /
        (c seqr pA13) / (g seqr pA14) / (g seqr pA11) / (u seqr pA13) }

val pA11: Parser<Int> = pA9  seqr u
val pA1 : Parser<Int> = pN / (a seqr pA11)
val pA12: Parser<Int> = pA9  seqr a
val pA13: Parser<Int> = pA9  seqr g
val pA14: Parser<Int> = pA9  seqr c

val pA2: Parser<Int> = fix { A2 -> pA1 seqr A2 / pN / a seqr pA11 / u seqr pA12 /
            c seqr pA13 / g seqr pA14 / g seqr pA11 / u seqr pA13 }

val pA3: Parser<Int> = fix { A3 -> pN / a seqr A3 / u seqr A3 / c seqr pA13 /
        g seqr pA14 / g seqr pA11 / u seqr pA13 }

val pA4: Parser<Int> = pA10 seqr pA3

fun createGrParser(): Parser<Int> {
    return conjp(pA1 seqr pA2, pA3 seqr pA4) map { it.first }
}
val grParser: Parser<Int> = createGrParser()