package net.podkopaev.grammar.PseudoknotsGrammar

import net.podkopaev.cpsComb.*

/**
 * L2 = { [^i (^j ]^i )^j | i, j > 0 }
 * S  -> S1 & S5
 * p1 -> [
 * p2 -> ]
 * p3 -> (
 * p4 -> )
 *
 * S1 -> S2 S3
 * S2 -> p1 S2 p2 | S4
 * S4 -> p3 S4 | eps
 * S3 -> p4 S3 | eps
 *
 * S5 -> S6 S7
 * S6 -> p1 S6 | eps
 * S7 -> p3 S7 p4 | S8
 * S8 -> p2 S8 | eps
 */

val p1 = terminal("[") map { 1 }
val p2 = terminal("]") map { 1 }
val p3 = terminal("(") map { 1 }
val p4 = terminal(")") map { 1 }

val pS3: Recognizer<Int> = fix { S3 -> (seq(p4, S3) map { t -> 1 + t.second }) / p4 }
val pS4: Recognizer<Int> = fix { S4 -> (seq(p3, S4) map { t -> 1 + t.second }) / p3 }
val pS2: Recognizer<Int> = fix { S2 -> (seq(seq(p1, S2), p2) map { t -> 1 + t.first.second }) / pS4 }

val pS6: Recognizer<Int> = fix { S6 -> (seq(p1, S6) map { t -> 1 + t.second }) / p1 }
val pS8: Recognizer<Int> = fix { S8 -> (seq(p2, S8) map { t -> 1 + t.second }) / p2 }
val pS7: Recognizer<Int> = fix { S7 -> (seq(seq(p3, S7), p4) map { t -> 1 + t.first.second }) / pS8 }

val grParser = and(seq(pS2, pS3), seq(pS6, pS7)) map { t -> t.first.first * 2 }