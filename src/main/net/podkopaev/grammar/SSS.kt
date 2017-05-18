package net.podkopaev.grammar
import net.podkopaev.cpsComb.*

/**
    S -> SSS | SS | a
 */

val a = terminal("a") map { 1 }
val pS: Recognizer<Int> = fix { S: Recognizer<Int> ->
    a / transp(seq(seq(S, S), S)) { p -> p.second + 1 } /
            transp(seq(S, S)) { p -> p.first + 1 } }