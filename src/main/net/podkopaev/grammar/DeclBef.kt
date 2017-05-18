package net.podkopaev.grammar.DeclBef
import net.podkopaev.cpsComb.*

/**
 * Declaration before use.
 *
S -> S d A | S c A & E d B | e;
A -> a A | e;
B -> a B a | E c;
E -> E c A | E d A | e;
 */

val a = terminal("a") map { 1 }
val c = terminal("c") map { 1 }
val d = terminal("d") map { 1 }
val e = eps map { 0 }

val pA: Recognizer<Int> = fix { A -> (seq(a, A) map { t -> 1 + t.second }) / e }
val pE: Recognizer<Int> = fix { E -> (seq(seq(E, c), pA) map { t -> 2 + t.first.first }) /
        (seq(seq(E, d), pA) map { p -> 2 + p.first.first }) / e }
val pB: Recognizer<Int> = fix { B -> (seq(seq(a, B), a) map { t -> 1 + t.first.second }) /
        (seq(pE, c) map { p -> 1 + p.first }) }

val pS: Recognizer<Int> = fix { S -> (seq(seq(S, d), pA) map { t -> 3 + t.first.first }) /
            (and(seq(seq(S, c), pA), seq(seq(pE, d), pB)) map { p -> 5 + p.first.second }) /
            e
}