package net.podkopaev.cpsComb

import java.util.*

abstract class Result<A>() {
    abstract fun showResult()
}

class Success<A>(val value: A, val endPos: Int) : Result<A>() {
    override fun showResult() {
        print("Success: {value=$value, endPos=$endPos}" + '\n')
    }
}

class Failure<A>(val endPos: Int) : Result<A>() {
    override fun showResult() {
        print("Failure: {endPos=$endPos}" + '\n')
    }
}

abstract class Parser<A>() {
    protected var parseString: String? = null
    private var analyzedPos: HashMap<Int, Result<A>>? = null

    internal open fun init(s: String) {
        parseString = s
        analyzedPos = HashMap()
    }

    fun get(s: String) {
        init(s)
        this(0) { r -> r.showResult()}
    }

    abstract fun parse(pos: Int, c: (Result<A>) -> Unit): Unit

    operator fun invoke(pos: Int, c: (Result<A>) -> Unit) {
        val memoizedRes = analyzedPos?.get(pos)
        if (memoizedRes != null) return c(memoizedRes)
        parse(pos) { parseRes -> analyzedPos?.put(pos, parseRes); c(parseRes) }
    }
    infix fun <B> map (f: (A) -> B ): Parser<B> = transp(this, f)
}

fun <A> conp(value: A): Parser<A> =
        object : Parser<A>() {
            override fun parse(pos: Int, c: (Result<A>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) { return c(Failure(pos)) }
                return c(Success(value, pos))
            }
        }

fun litp(str: String): Parser<String> =
        object : Parser<String>() {
            override fun parse(pos: Int, c: (Result<String>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) { return c(Failure(pos)) }
                val substring = parseString!!.substring(pos)
                if (!substring.startsWith(str)) {
                    c(Failure(pos))
                    return
                }
                c(Success(str, str.length + pos))
            }
        }

fun satp(cond: (Char) -> Boolean): Parser<Char> =
        object : Parser<Char>() {
            override fun parse(pos: Int, c: (Result<Char>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) { return c(Failure(pos)) }
                val substring = parseString!!.substring(pos)
                if (substring.length < 1 || !cond(substring[0])) {
                   return c(Failure(pos))
                }
                return c(Success(substring[0], pos + 1))
            }
        }

fun <A, B> bindp(left: Parser<A>, right: (A) -> Parser<B>): Parser<B> =
        object : Parser<B>() {
            override fun parse(pos: Int, c: (Result<B>) -> Unit) {
                left(pos) { r ->
                    when (r) {
                        is Success<A> -> {
                            right(r.value)(r.endPos, c)
                        }
                        is Failure<A> -> {
                            c(Failure(pos))
                        }
                    }
                }
            }
        }
fun <A> seqp(left: Parser<A>, right: Parser<A>) =
        bindp(left) { right }

fun <A, B> seq(left: Parser<A>, right: Parser<B>): Parser<Pair<Result<A>, Result<B>>> =
    object : Parser<Pair<Result<A>, Result<B>>>() {
        override fun parse(pos: Int, c: (Result<Pair<Result<A>, Result<B>>>) -> Unit) {
            left(pos) { r ->
                when (r) {
                    is Success<A> -> {
                        right(r.endPos) { r2 ->
                            c(Success(Pair(r, r2), pos))
                        }
                    }
                    is Failure<A> -> {
                        c(Failure(pos))
                    }
                }
            }

        }
        override fun init(s: String) {
            super.init(s)
            left.init(s)
            right.init(s)
        }
    }

fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> =
        object : Parser<B>() {
            override fun parse(pos: Int, c: (Result<B>) -> Unit) {
                parser(pos) { r ->
                    when (r) {
                        is Success<A> -> {
                            c(Success(f(r.value), r.endPos))
                        }
                        is Failure<A> -> {
                            c(Failure(pos))
                        }
                    }
                }
            }
            override fun init(s: String) {
                super.init(s)
                parser.init(s)
            }
        }

fun <A> many0(parser: Parser<A>): Parser<List<A>> =
    object : Parser<List<A>>() {
        override fun parse(pos: Int, c: (Result<List<A>>) -> Unit) {
            parser(pos) { r ->
                when (r) {
                    is Success<A> -> {
                        this(r.endPos) {
                            r2 ->
                            when (r2) {
                                is Success<List<A>> -> {
                                    val v = LinkedList<A>()
                                    v.add(r.value)
                                    v.addAll(r2.value)
                                    c(Success(v, r2.endPos))
                                }
                                is Failure<List<A>> -> {
                                    c(Failure(pos))
                                }
                            }
                        }
                    }
                    is Failure<A> -> {
                        c(Failure(pos))
                    }
                }
            }
        }
        override fun init(s: String) {
            super.init(s)
            parser.init(s)
        }
    }

fun <A> many1(parser: Parser<A>): Parser<List<A>> =
        transp(seq(parser, many0(parser))) { aal: Pair<Result<A>, Result<List<A>>> ->
            val v = LinkedList<A>()
            val r0 = aal.first
            when (r0) {
                is Success<A> -> { v.add(r0.value) }
            }

            val r = aal.second
            when(r) {
                is Success<List<A>> -> {
                    v.addAll(r.value)
                }
                is Failure<List<A>> -> { }
            }
            v
        }

fun <A> disjp(left: Parser<A>, right: Parser<A>): Parser<A> =
        object : Parser<A>() {
            override fun parse(pos: Int, c: (Result<A>) -> Unit) {
                left(pos, c)
                right(pos, c)
            }
            override fun init(s: String) {
                super.init(s)
                left.init(s)
                right.init(s)
            }
        }

val digit: Parser<Char> = satp { ('0'..'9').contains(it) }
val alpha: Parser<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
fun List<Char>.toStr(): String = String(toCharArray())
val number: Parser<Int> = many1(digit) map { it.toStr().toInt() }