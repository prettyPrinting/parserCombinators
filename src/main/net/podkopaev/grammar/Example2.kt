package net.podkopaev.grammar.Example2

import net.podkopaev.booleanComb.*
 /*
 Conjunctive grammar for language { w c w | w in {a, b}* }
 S -> C & D
 C -> XCX | c
 D -> aA & aD | bB & bD | cE
 A -> XAX | cEa
 B -> XBX | cEb
 E -> XE  | epsilon
 X -> a   | b
 */

val a: Parser<Char> = char('a')
val b: Parser<Char> = char('b')
val c: Parser<Char> = char('c')
val k: Parser<Char> = char('k')

val pX: Parser<Char> = a / b
val pC: Parser<Char> = fix { C ->
    (pX seqr C seqr pX) / c
}
val pE: Parser<Char> = fix { E ->
    (pX seqr E) / k
}
val pA: Parser<Char> = fix { A ->
    (pX seqr A seqr pX) / (c seqr pE seqr a)
}
val pB: Parser<Char> = fix { B ->
    (pX seqr B seqr pX) / (c seqr pE seqr b)
}
val pD: Parser<Char> = fix { D ->
    transp(conjp(a seqr pA, a seqr D)) {p -> p.first} /
            transp(conjp(b seqr pB, b seqr D)) {p  -> p.first} /
            (c seqr pE)
}
val grParser = conjp(pC, pD)
