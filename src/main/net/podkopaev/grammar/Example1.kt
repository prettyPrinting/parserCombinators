package net.podkopaev.grammar.Example1

import net.podkopaev.cpsComb.*
/*
Conjunctive grammar for language {a^n b^n c^n}
S  -> AB & DC           {a^i b^j c^k | j = k} &
                        {a^i b^j c^k | i = j}
A  -> aA  | eps     {a*}
B  -> bBc | eps     {b^n c^n}

D  -> aDb | eps     {a^k b^k}
C  -> cC  | eps     {c*}
 */

val a = terminal("a") map { 1 }
val b = terminal("b") map { 1 }
val c = terminal("c") map { 1 }

//Grammar for {a^n b^n c^n}
val pA = fix { A: Recognizer<Int> -> a / transp(seq(a, A)) { p -> 1 + p.second } }
val pB = fix { B: Recognizer<Int> -> transp(seq(seq(b, B), c)) { p -> 1 + p.first.second } /
        transp(seq(b, c)) { 1 } }
val pC = fix { C: Recognizer<Int> -> transp(seq(c, C)) { p -> 1 + p.second } / transp(c) { 1 } }
val pD = fix { D: Recognizer<Int> -> transp(seq(seq(a, D), b)) { p -> 1 + p.first.second } /
        transp(seq(a, b)) { 1 } }
val p = and(seq(pA, pB), seq(pD, pC))

val grParser = transp(p, {
    if (it.first != it.second) { throw Exception("Not equal number of symbols!") }
    it.first.first
})