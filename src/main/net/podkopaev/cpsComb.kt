package net.podkopaev.cpsComb

import java.util.*

typealias K<A>         = (Int, A) -> Unit
typealias CPSResult<A> = (K<A>)   -> Unit

class Call<A>(val k: K<A>, val pos: Int, val res: A) : Runnable {
    override fun run(): Unit = k(pos, res)
}
class Seq<A>(val r: () -> CPSResult<A>, val k: K<A>) : Runnable {
    override fun run(): Unit = r()(k)
}
class Alt<A>(
        val lhs: CPSResult<A>, val rhs: () -> CPSResult<A>, val k: K<A>
) : Runnable {
    override fun run(): Unit {
        Trampoline.jobs.push(Seq(rhs, k))
        lhs(k)
    }
}

object Trampoline {
    var jobs: Deque<Runnable> = ArrayDeque<Runnable>()
    fun <A> call(k: K<A>, pos: Int, res: A): Unit =
            Trampoline.jobs.push(Call(k, pos, res))
    fun <A> alt(lhs: CPSResult<A>, k: K<A>, rhs: () -> CPSResult<A>): Unit =
            Trampoline.jobs.push(Alt(lhs, rhs, k))
    fun run(): Unit {
        while(!jobs.isEmpty()) jobs.pop().run()
    }
}

abstract class Recognizer<A>: (Int) -> CPSResult<A> {
    var input: String? = null
    override abstract operator fun  invoke(p: Int): CPSResult<A>

    internal open fun init(s: String) {
        input = s
    }
    fun <A> parse(s: String, p: Recognizer<A>): A? {
        p.init(s)
        var result: A? = null
        val k0: K<A> = { pos, res ->
            if (pos == s.length) result = res
        }
        p(0)(k0)
        Trampoline.run()
        return result
    }
}

fun <A> memo_k(f: K<A>): K<A> {
    val s = HashSet<Pair<Int, A>>()
    return { p : Int, res : A ->
        val pair = Pair(p, res)
        if (!s.contains(pair)) {
            s += pair
            f(p, res)
        }
    }
}

fun <A> result(f: CPSResult<A>): CPSResult<A> = f

fun <A> success(pos: Int, res: A): CPSResult<A> = result { k -> k(pos, res) }
fun <A> failure()                : CPSResult<A> = result { }

fun <A> memo_result(lazy_result: () -> CPSResult<A>): CPSResult<A> {
    val Ks: Deque<K<A>> = ArrayDeque<K<A>>()
    var Rs: Set<Pair<Int, A>> = LinkedHashSet<Pair<Int, A>>()

    return { k ->
            if (Ks.isEmpty()) {
                Ks.push(k)
                val ki: K<A> = { pos : Int, res : A ->
                    val pair = Pair(pos, res)
                    if (!Rs.contains(pair)) {
                        Rs += pair
                        val iter = Ks.iterator()
                        while(iter.hasNext()) Trampoline.call(iter.next(), pos, res)
                    }
                }
                lazy_result()(ki)
            } else {
                Ks.push(k)
                val iter = Rs.iterator()
                while (iter.hasNext()) {
                    val (pos, res) = iter.next()
                    Trampoline.call(k, pos, res)
                }
            }
    }
}

class Memo<A>(val f: Recognizer<A>): Recognizer<A>() {
    val table: MutableMap<Int, CPSResult<A>> = HashMap()

    override fun invoke(p: Int): CPSResult<A> =
            table.getOrPut(p) { memo_result { f(p) } }

    override fun init(s: String) {
        super.init(s)
        f.init(s)
    }
}
fun <A> memo(f: Recognizer<A>): Recognizer<A> = Memo(f)

operator fun <A> (Recognizer<A>).div (p: Recognizer<A>): Recognizer<A> = rule (this, p)

internal fun <A> parser(r: Recognizer<A>.(Int) -> CPSResult<A>): Recognizer<A> =
        object : Recognizer<A>() {
            override fun invoke(p: Int): CPSResult<A>  = r(p)
        }

fun <A> (CPSResult<A>).orElse(rhs: () -> CPSResult<A>): CPSResult<A> =
        result { k -> Trampoline.alt(this, k, rhs) }

class Top private constructor () {
    companion object {
        val top: Top = Top()
    }
}
val eps: Recognizer<Top> = parser { i -> success(i, Top.top) }

fun <A, B> (CPSResult<A>).map(f: (A) -> B): CPSResult<B> =
        result { k ->
            this(memo_k { pos, res -> k(pos, f(res)) })
        }

class TransParser<A, B>(
        val parser: Recognizer<A>, val f: (A) -> B
): Recognizer<B>() {
    override fun invoke(p: Int): CPSResult<B> =
        parser(p).map(f)

    override fun init(s: String) {
        super.init(s)
        parser.init(s)
    }
}
fun <A, B> transp(parser: Recognizer<A>, f: (A) -> B): Recognizer<B> =
        TransParser(parser, f)

class SeqP<A, B>(val r1: Recognizer<A>, val r2: Recognizer<B>): Recognizer<Pair<A, B>>() {
    override fun invoke(p: Int): CPSResult<Pair<A, B>> = { kpair : K<Pair<A, B>> ->
        val r1res = r1(p)
        r1res { pos, res1 ->
            val r2res = r2(pos)
            r2res { pos, res2 ->
                kpair(pos, Pair(res1, res2))
            }
        }
    }

    override fun init(s: String) {
        super.init(s)
        r1.init(s)
        r2.init(s)
    }
}

fun <A, B> seq(r1: Recognizer<A>, r2: Recognizer<B>): Recognizer<Pair<A, B>> = SeqP(r1,r2)

fun <A> rule(r1: Recognizer<A>, r2: Recognizer<A>): Recognizer<A> = memo(DisjP(r1, r2))

class DisjP<A>(val r1: Recognizer<A>, val r2: Recognizer<A>): Recognizer<A>() {
    override fun invoke(p: Int): CPSResult<A> = r1(p).orElse{ r2(p) }
    override fun init(s: String) {
        super.init(s)
        r1.init(s)
        r2.init(s)
    }
}

class ProxyParserNotSetException(): Exception()
class ProxyRecognizer<A> (var recognizer: Recognizer<A>?) : Recognizer<A>() {
    override fun invoke(p: Int): CPSResult<A>  {
        val parseStringVal = input
        if (parseStringVal != null) {
            init(parseStringVal)
        }
        return recognizer?.invoke(p) ?: throw ProxyParserNotSetException()
    }
    override fun init(s: String) {
        super.init(s)
    }
}

fun <A> plain_fix(f : (Recognizer<A>) -> Recognizer<A>): Recognizer<A> {
    val proxy = ProxyRecognizer<A>(null)
    val result = f (proxy)
    proxy.recognizer = result
    return result
}

fun <A> fix(f: (Recognizer<A>) -> Recognizer<A>) : Recognizer<A> {
    val mf = { p : Recognizer<A> -> memo(f(p)) }
    return plain_fix(mf)
}

class And <A, B> (val p1: Recognizer<A>, val p2: Recognizer<B>) : Recognizer<Pair<A, B>>() {
    override fun invoke(p: Int): CPSResult<Pair<A, B>> {
        val passedByp1: ArrayList<Pair<Int, A>> = ArrayList()
        val passedByp2: ArrayList<Pair<Int, B>> = ArrayList()
        val executed  : ArrayList<Pair<Int, Pair<A, B>>> = ArrayList()

        return { k: K<Pair<A, B>> ->
            p1(p)({ pos: Int, res: A ->
                val pair1p = Pair(pos, res)
                passedByp1.add(pair1p)
                passedByp2.forEach { r ->
                    if (pair1p.first == r.first) {
                        val kpair = Pair(pair1p.second, r.second)
                        k(pos, kpair)
                        executed.add(Pair(pos, kpair))
                    }
                }
            })

            p2(p)({ pos: Int, res: B ->
                val pair2p = Pair(pos, res)
                passedByp2.add(pair2p)
                passedByp1.forEach { r ->
                    if (pair2p.first == r.first) {
                        val kpair = Pair(r.second, pair2p.second)
                        k(pos, kpair)
                        executed.add(Pair(pos, kpair))
                    }
                }
            })
        }
    }
    override fun init(s: String) {
        super.init(s)
        p1.init(s)
        p2.init(s)
    }
}

fun <A, B> and(p1: Recognizer<A>, p2: Recognizer<B>): Recognizer<Pair<A, B>> = And(p1, p2)

fun terminal(t : String): Recognizer<String> = parser {
    i -> if (input?.startsWith(t, i) ?: false) success(i + t.length, t) else failure()
}

fun satp(cond: (Char) -> Boolean): Recognizer<Char> = parser { i ->
    val symbol = input?.get(i) ?: return@parser failure()
    if (cond(symbol)) { return@parser success(i + 1, symbol) }
    failure()
}
fun char(c: Char): Recognizer<Char> = satp { it == c }
val space : Recognizer<Char> =
        char(' ') / char('\n') / char('\t') /
                transp(terminal("\r\n")) { '\n' }
val spaces: Recognizer<Char> = fix { s -> space / transp(seq(space, s), {' '}) }

val digit: Recognizer<Char> = satp { ('0'..'9').contains(it) }
val alpha: Recognizer<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
val alphaOrDigit: Recognizer<Char> = alpha / digit