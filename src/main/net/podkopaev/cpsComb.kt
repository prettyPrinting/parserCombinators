package net.podkopaev.cpsComb

import java.util.*

abstract class Result<A>() { }
class Success<A>(val value: A, val rest: Int) : Result<A>() { }

class Failure<A>(rest: Int) : Result<A>() { }

abstract class Parser<A>() {
    var parseString: String? = null

    internal open fun init(s: String) {
        parseString = s
    }

    abstract operator fun invoke(pos: Int, c: (Result<A>) -> Unit): Unit
}

fun <A> conp(value: A): Parser<A> =
        object : Parser<A>() {
            override fun invoke(pos: Int, c: (Result<A>) -> Unit) {
                c(Success(value, pos))
            }
        }

fun litp(str: String): Parser<String> =
        object : Parser<String>() {
            override fun invoke(pos: Int, c: (Result<String>) -> Unit) {
                val substring = parseString!!.substring(pos)
                if (!substring.startsWith(str)) {
                    c(Failure(pos))
                }
                c(Success(str, pos))
            }
        }

fun satp(cond: (Char) -> Boolean): Parser<Char> =
        object : Parser<Char>() {
            override fun invoke(pos: Int, c: (Result<Char>) -> Unit) {
                val substring = parseString!!.substring(pos)
                if (substring.length < 1 || !cond(substring[0])) {
                    c(Failure(pos))
                }
                c(Success(substring[0], 1))
            }
        }

fun <A, B> bindp(left: Parser<A>, right: (A) -> Parser<B>): Parser<B> =
        object : Parser<B>() {
            override fun invoke(pos: Int, c: (Result<B>) -> Unit) {
                left(pos) { r ->
                    when (r) {
                        is Success<A> -> {
                            right(r.value)(r.rest, c)
                        }
                        is Failure<A> -> {
                            c(Failure(pos))
                        }
                    }
                }
            }
        }
fun <A> seqp(left: Parser<A>, right: Parser<A>) =
        object : Parser<A>() {
            override fun invoke(pos: Int, c: (Result<A>) -> Unit) {
                bindp(left) { right }
            }
        }

fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> =
        bindp(parser) { conp(f(it)) }

fun <A> disjp(left: Parser<A>, right: Parser<A>): Parser<A> =
        object : Parser<A>() {
            override fun invoke(pos: Int, c: (Result<A>) -> Unit) {
                left(pos, c)
                right(pos, c)
            }
        }

val digit: Parser<Char> = satp { ('0'..'9').contains(it) }
val alpha: Parser<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}