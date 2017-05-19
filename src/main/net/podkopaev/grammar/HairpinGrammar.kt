package net.podkopaev.grammar.HairpinGrammar

import net.podkopaev.cpsComb.*
/**
 * A context-free grammar for an RNA stem loop.
 * S  -> aW1u | cW1g | gW1c | uW1a
 * W1 -> aW2u | cW2g | gW2c | uW2a
 * W2 -> aW3u | cW3g | gW3c | uW3a
 * W3 -> gaaa | gcaa
 *
 */

val a = terminal("a") map { 1 }
val u = terminal("u") map { 1 }
val c = terminal("c") map { 1 }
val g = terminal("g") map { 1 }

val pl = { t:Pair<Pair<Int, Int>, Int> -> t.first.second + 2 }

val pW3: Recognizer<Int> = (seq(seq(seq(g, a), a), a) map { t -> t.first.first.second + 3 }) /
        (seq(seq(seq(g, c), a), a) map { t -> t.first.first.second + 3 })

val pW2: Recognizer<Int> = (seq(seq(a, pW3), u) map pl) / (seq(seq(c, pW3), g) map pl) /
        (seq(seq(g, pW3), c) map pl) / (seq(seq(u, pW3), a) map pl)

val pW1: Recognizer<Int> = (seq(seq(a, pW2), u) map pl) / (seq(seq(c, pW2), g) map pl) /
        (seq(seq(g, pW2), c) map pl) / (seq(seq(u, pW2), a) map pl)

val pS: Recognizer<Int> = (seq(seq(a, pW1), u) map pl) / (seq(seq(c, pW1), g) map pl) /
        (seq(seq(g, pW1), c) map pl) / (seq(seq(u, pW1), a) map pl)
