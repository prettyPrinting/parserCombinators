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
val k = char('k')

val pA: Parser<Char> = fix { A ->
    (a seqr A) / k
}
val pB: Parser<Char> = fix { B ->
    (b seqr B seqr c) / k
}
val pC: Parser<Char> = fix { C ->
    (c seqr C) / k
}
val pD: Parser<Char> = fix { D ->
    (a seqr D seqr b) / k
}
val grParser = pA seqr pB // {a* k b ^n k c^n}