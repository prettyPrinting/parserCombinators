package net.podkopaev.cpsComb

import java.util.*

abstract class Parser<A>() {
    protected var parseString: String? = null
    private var analyzedPos: HashMap<Int, List<Pair<Int, A>>>? = null

    internal open fun init(s: String) {
        parseString = s
        analyzedPos = HashMap()
    }

    abstract fun parse(pos: Int, c: (List<Pair<Int, A>>) -> Unit): Unit

    infix fun <B> map (f: (A) -> B ): Parser<B> = transp(this, f)
    infix fun <B> seqr(p: Parser<B>): Parser<B> = seqrp (this, p)
    infix fun <B> seql(p: Parser<B>): Parser<A> = seqlp (this, p)

    operator fun     div  (p: Parser<A>): Parser<A>          = disjp (this, p)
    operator fun <B> plus (p: Parser<B>): Parser<Pair<A, B>> = seq   (this, p)

    operator fun invoke(pos: Int, c: (List<Pair<Int, A>>) -> Unit) {
        val memoizedRes = analyzedPos?.get(pos)
        if (memoizedRes != null) return c(memoizedRes)
        parse(pos) { parseRes -> analyzedPos?.put(pos, parseRes); c(parseRes) }
    }

    fun get(s: String) {
        init(s)
        this(0) { result -> result.find { it.first == s.length } ?.second }
    }
}

fun <A> conp(value: A): Parser<A> =
        object : Parser<A>() {
            override fun parse(pos: Int, c: (List<Pair<Int, A>>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) {
                    return c(listOf())
                }
                c(listOf(Pair(pos, value)))
            }
        }

fun litp(str: String): Parser<String> =
        object : Parser<String>() {
            override fun parse(pos: Int, c: (List<Pair<Int, String>>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) { return c(listOf()) }
                val substring = parseString!!.substring(pos)
                if (!substring.startsWith(str)) {
                    return c(listOf())
                }
                c(listOf(Pair(pos + str.length, str)))
            }
        }

fun satp(cond: (Char) -> Boolean): Parser<Char> =
        object : Parser<Char>() {
            override fun parse(pos: Int, c: (List<Pair<Int, Char>>) -> Unit) {
                if (pos >= parseString?.length ?: -1 || pos < 0) { return c(listOf()) }
                val substring = parseString!!.substring(pos)
                if (substring.length < 1 || !cond(substring[0])) {
                   return c(listOf())
                }
                c(listOf(Pair(pos + 1,  substring[0])))
            }
        }

fun <A, B> seq(left: Parser<A>, right: Parser<B>): Parser<Pair<A, B>> =
        object : Parser<Pair<A, B>>() {
            override fun parse(pos: Int, c: (List<Pair<Int, Pair<A, B>>>) -> Unit) {
                val result = ArrayList<Pair<Int, Pair<A, B>>>()
                left(pos) { l ->
                    l.forEach { pa ->
                        right(pa.first) { rightRes ->
                            rightRes.forEach { pb ->
                                result.add(Pair(pb.first, Pair(pa.second, pb.second)))
                            }
                        }
                    }
                }
                c(result)
            }

    override fun init(s: String) {
        super.init(s)
        left.init(s)
        right.init(s)
    }
}

fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> =
        object : Parser<B>() {
            override fun parse(pos: Int, c: (List<Pair<Int, B>>) -> Unit) {
                val result = ArrayList<Pair<Int, B>>()
                parser(pos) { r ->
                    r.forEach { pa ->
                        result.add(Pair(pa.first, f(pa.second)))
                    }
                }
                c(result)
            }

    override fun init(s: String) {
        super.init(s)
        parser.init(s)
    }
}

fun <A, B> seqrp(left: Parser<A>, right: Parser<B>): Parser<B> =
        transp(seq(left, right)) { p -> p.second }

fun <A, B> seqlp(left: Parser<A>, right: Parser<B>): Parser<A> =
        transp(seq(left, right)) { p -> p.first }

open class Many0Parser<A>(
        val parser: Parser<A>
): Parser<List<A>>() {
    override fun parse(pos: Int, c: (List<Pair<Int, List<A>>>) -> Unit) {
        val result = ArrayList<Pair<Int, List<A>>>()
        result.add(Pair(pos, listOf()))
        parser(pos) { pr ->
            pr.forEach { pp ->
                this(pp.first) { r ->
                    r.forEach { pt ->
                        val v = LinkedList<A>()
                        v.add(pp.second)
                        v.addAll(pt.second)
                        result.add(Pair(pt.first, v))
                    }
                }
            }
        }
        c(result)
    }

    override fun init(s: String) {
        super.init(s)
        parser.init(s)
    }
}
fun <A> many0(parser: Parser<A>): Parser<List<A>> = Many0Parser(parser)

fun <A> many1(parser: Parser<A>): Parser<List<A>> =
        transp(seq(parser, many0(parser))) { aal ->
            val v = LinkedList<A>()
            v.add(aal.first)
            v.addAll(aal.second)
            v
        }

fun <A> disjp(left: Parser<A>, right: Parser<A>): Parser<A> =
    object : Parser<A>() {
        override fun parse(pos: Int, c: (List<Pair<Int, A>>) -> Unit) {
            left(pos) { r -> r + right(pos) { r2 -> c(r2) } }
        }

        override fun init(s: String) {
            super.init(s)
            left.init(s)
            right.init(s)
        }
    }

class ConjParser<A, B>(
        val left: Parser<A>, val right: Parser<B>
): Parser<Pair<A, B>>() {
    override fun parse(pos: Int, c: (List<Pair<Int, Pair<A, B>>> ) -> Unit) {
        val result = ArrayList<Pair<Int, Pair<A, B>>>()
        left(pos) { leftRes ->
            right(pos) { rightRes ->
                leftRes.forEach { lr ->
                    rightRes.forEach { rr ->
                        if (rr.first == lr.first) {
                            result.add(Pair(lr.first, Pair(lr.second, rr.second)))
                        }
                    }
                }
            }
        }
        c(result)
    }

    override fun init(s: String) {
        super.init(s)
        left.init(s)
        right.init(s)
    }
}
fun <A, B> conjp(left: Parser<A>, right: Parser<B>): Parser<Pair<A, B>> =
        ConjParser(left, right)

class ConjNotParser<A, B>(
        val left: Parser<A>, val right: Parser<B>
): Parser<A>() {
    override fun parse(pos: Int, c: (List<Pair<Int, A>>) -> Unit) {
        val result = ArrayList<Pair<Int, A>>()
        left(pos) { leftRes ->
            right(pos) { rightRes ->
                leftRes.forEach { lr ->
                    if (rightRes.find { it.first == lr.first } == null) {
                        result.add(lr)
                    }
                }
            }
        }
        c(result)
    }

    override fun init(s: String) {
        super.init(s)
        left.init(s)
        right.init(s)
    }
}
fun <A, B> conjNotp(left: Parser<A>, right: Parser<B>): Parser<A> =
        ConjNotParser(left, right)

fun char(c: Char): Parser<Char> = satp { it == c }
val digit: Parser<Char> = satp { ('0'..'9').contains(it) }
val alpha: Parser<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
fun List<Char>.toStr(): String = String(toCharArray())
val number: Parser<Int> = many1(digit) map { it.toStr().toInt() }