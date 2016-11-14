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

val a = char('a')
val b = char('b')
val c = char('c')
val epsilon = conp('e')

val pX: Parser<Char> = a / b
val pC: Parser<Char> = fix { C ->
    (pX seqr C seqr pX) / c
}
val pE: Parser<Char> = fix { E ->
    (pX seqr E seqr pX) / pX / epsilon
}
val pA: Parser<Char> = fix { A ->
    (pX seqr A seqr pX) / (c seqr pE seqr a)
}
val pB: Parser<Char> = fix { B ->
    (pX seqr B seqr pX) / (c seqr pE seqr b)
}
val pD: Parser<Char> = fix { D ->
    transp(conjp(a seqr pA, a seqr D)) { p -> p.first } /
            transp(conjp(b seqr pB, b seqr D)) { p  -> p.first } /
            (c seqr pE)
}
val grParser = conjp(pC, pD)