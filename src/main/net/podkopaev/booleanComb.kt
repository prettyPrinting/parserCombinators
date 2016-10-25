package net.podkopaev.booleanComb

import java.util.*

class ParserNotInitializedException(): Exception()
class ProxyParserNotSetException(): Exception()

abstract class Parser<A>() {
    protected var parseString: String? = null
    abstract protected fun parse(pos: Int): List<Pair<Int, A>>

    private var analyzedPos: HashMap<Int, List<Pair<Int, A>>>? = null

    internal open fun init(s: String) {
        parseString = s
        analyzedPos = HashMap()
    }

    operator fun invoke(pos: Int): List<Pair<Int, A>> {
        val memoizedRes = analyzedPos?.get(pos)
        if (memoizedRes != null) return memoizedRes
        val parseRes = parse(pos)
        analyzedPos?.put(pos, parseRes)
        return parseRes
    }

    operator fun <B> minus(p: Parser<B>): Parser<A>          = seqlp (this, p)
    operator fun <B> plus (p: Parser<B>): Parser<Pair<A, B>> = seq   (this, p)
    operator fun     div  (p: Parser<A>): Parser<A>          = disjp (this, p)

    infix fun <B> map (f: (A) -> B ): Parser<B> = transp(this, f)
    infix fun <B> seql(p: Parser<B>): Parser<A> = seqlp (this, p)
    infix fun <B> seqr(p: Parser<B>): Parser<B> = seqrp (this, p)

    fun get(s: String): A? {
        init(s)
        val result = this(0)
        return result.find { it.first == s.length } ?.second
    }
}

internal fun <A> parser(parse: Parser<A>.(String) -> List<Pair<Int, A>>): Parser<A> =
    object : Parser<A>() {
        override fun parse(pos: Int): List<Pair<Int, A>> {
            if (pos >= parseString?.length ?: -1 || pos < 0) { return listOf() }
            return parse(parseString?.substring(pos)
                    ?: throw ParserNotInitializedException())
                    .map { pa -> Pair(pa.first + pos, pa.second) }
        }
    }

fun <A> conp(value: A): Parser<A> = parser {
    listOf(Pair(0, value))
}

fun litp(str: String): Parser<String> = parser { input ->
    if (!input.startsWith(str)) {
        return@parser listOf()
    }
    return@parser listOf(Pair(str.length, str))
}

class TransParser<A, B>(
        val parser: Parser<A>, val f: (A) -> B
): Parser<B>() {
    override fun parse(pos: Int): List<Pair<Int, B>> {
        return parser(pos).map { pa -> Pair(pa.first, f(pa.second)) }
    }

    override fun init(s: String) {
        super.init(s)
        parser.init(s)
    }
}
fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> =
        TransParser(parser, f)

class DisjParser<A>(
        val left: Parser<A>, val right: Parser<A>
): Parser<A>() {
    override fun parse(pos: Int): List<Pair<Int, A>> {
        return left(pos) + right(pos)
    }

    override fun init(s: String) {
        super.init(s)
        left.init(s)
        right.init(s)
    }
}
fun <A> disjp(left: Parser<A>, right: Parser<A>): Parser<A> =
        DisjParser(left, right)

fun satp(cond: (Char) -> Boolean): Parser<Char> = parser { input ->
    if (input.length < 1 || !cond(input[0])) {
        return@parser listOf()
    }
    return@parser listOf(Pair(1, input[0]))
}

class SeqParser<A, B>(
        val left: Parser<A>, val right: Parser<B>
): Parser<Pair<A, B>>() {
    override fun parse(pos: Int): List<Pair<Int, Pair<A, B>>> {
        val leftRes = left(pos)
        val result = ArrayList<Pair<Int, Pair<A, B>>>()
        leftRes.forEach { pa ->
            val rightRes = right(pa.first)
            rightRes.forEach {  pb ->
                result.add(Pair(pb.first, Pair(pa.second, pb.second)))
            }
        }
        return result
    }

    override fun init(s: String) {
        super.init(s)
        left.init(s)
        right.init(s)
    }
}
fun <A, B> seq(left: Parser<A>, right: Parser<B>): Parser<Pair<A, B>> =
        SeqParser(left, right)

fun <A, B> seqlp(left: Parser<A>, right: Parser<B>): Parser<A> =
        transp(seq(left, right)) { p -> p.first }

fun <A, B> seqrp(left: Parser<A>, right: Parser<B>): Parser<B> =
        transp(seq(left, right)) { p -> p.second }

class Many0Parser<A>(
        val parser: Parser<A>
): Parser<List<A>>() {
    override fun parse(pos: Int): List<Pair<Int, List<A>>> {
        val result = ArrayList<Pair<Int, List<A>>>()
        result.add(Pair(pos, listOf()))

        val parserResult = parser(pos)
        parserResult.forEach { pp ->
            this(pp.first).forEach { pt ->
                val v = LinkedList<A>()
                v.add(pp.second)
                v.addAll(pt.second)
                result.add(Pair(pt.first, v))
            }
        }

        return result
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

class ProxyParser<A>(): Parser<A>() {
    // TODO: simplify ProxyParser, i.e. remove intermediate table (analyzedPos)
    var parser: Parser<A>? = null
        set(newParser) {
            field = newParser
            val parseStringVal = parseString
            if (parseStringVal != null) {
                init(parseStringVal)
            }
        }

    override fun parse(pos: Int) = parser?.invoke(pos) ?: throw ProxyParserNotSetException()

    // NOTE: It doesn't initialize the embedded parser!
    override fun init(s: String) { super.init(s) }
}
fun <A> proxy(): ProxyParser<A> = ProxyParser()

fun char(c: Char): Parser<Char> = satp { it == c }
val digit: Parser<Char> = satp { ('0'..'9').contains(it) }
val alpha: Parser<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
val alphaOrDigit: Parser<Char> = alpha / digit

fun List<Char>.toStr(): String = String(toCharArray())
val symbol: Parser<String> =
        (alpha + many0(alphaOrDigit)) map {
            val sb = StringBuilder()
            sb.append(it.first)
            it.second.forEach { sb.append(it) }
            sb.toString()
        }
val number: Parser<Int>    = many1(digit) map { it.toStr().toInt() }
val word  : Parser<String> = many1(alpha) map { it.toStr() }

fun <A, B, C> gparen(leftparen: Parser<A>, p: Parser<B>, rightparen: Parser<C>): Parser<B> =
        seqrp(leftparen, p) - rightparen
fun <A> paren (p: Parser<A>): Parser<A> = gparen(litp("("), p, litp(")"))
fun <A> cparen(p: Parser<A>): Parser<A> = gparen(litp("{"), p, litp("}"))

val space : Parser<Char> =
        char(' ') / char('\n') / char('\t') /
        (litp("\r\n") map { '\n' })
val spaces: Parser<String> = many0(space) map { it.toStr() }
fun <A> sp(p: Parser<A>): Parser<A> = gparen(spaces, p, spaces)

fun <A> leftAssocp(opp: Parser<String>, elemp: Parser<A>, f: (String, A, A) -> A): Parser<A> {
    val rightp = opp + elemp
    val rightLp: Parser<List<Pair<String, A>>> = many0(rightp)
    return (elemp + rightLp) map { el ->
        el.second.fold(el.first) { e, t -> f(t.first, e, t.second) }
    }
}
