package net.podkopaev.grammar.Example1

import net.podkopaev.booleanComb.*
/*
Conjunctive grammar for language {a^n b^n c^n}
S  -> AB & DC           {a^i b^j c^k | j = k} &
                        {a^i b^j c^k | i = j}
A  -> aA  | epsilon     {a*}
B  -> bBc | epsilon     {b^n c^n}
C  -> cC  | epsilon     {c*}
D  -> aDb | epsilon     {a^k b^k}
 */

val a = char('a')
val b = char('b')
val c = char('c')
val epsilon = conp('e')

val pA: Parser<Char> = fix { A ->
    (a seqr A seqr a) / a / epsilon
}
val pB: Parser<Char> = fix { B ->
    (b seqr B seqr c) / epsilon
}
val pC: Parser<Char> = fix { C ->
    (c seqr C seqr c) / c / epsilon
}
val pD: Parser<Char> = fix { D ->
    (a seqr D seqr b) / epsilon
}
val grParser = conjp(pA seqr pB, pD seqr pC)