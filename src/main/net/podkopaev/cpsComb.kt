package net.podkopaev.cpsComb

import java.util.*

typealias K<A> = (A) -> Unit

abstract class Recognizer<A>: (A) -> CPSResult<A> {
    var input: String? = null
    override abstract operator fun  invoke(p: A): CPSResult<A>

    internal open fun init(s: String) {
        input = s
    }
}

operator fun <A> (Recognizer<A>).div (p: Recognizer<A>): Recognizer<A> = rule (this, p)

internal fun <A> parser(r: Recognizer<A>.(A) -> CPSResult<A>): Recognizer<A> =
        object : Recognizer<A>() {
            override fun invoke(p: A): CPSResult<A>  = r(p)
        }

abstract class CPSResult<A>: (K<A>) -> Unit {
    var input: String? = null
    override abstract operator fun  invoke(k: K<A>) : Unit
    internal open fun init(s: String) {
        input = s
    }
}

fun <A> memo_k(f: K<A>): K<A> {
    val s: HashSet<A> = HashSet()
    return { t -> if(!s.contains(t)) { s += t; f(t) } }
}

fun <A> result(f: (K<A>) -> Unit): CPSResult<A> =
        object : CPSResult<A>() {
            override fun invoke(k: K<A>) = f(k)
        }

fun <A> success(t: A): CPSResult<A> = result { k -> k(t) }
fun <A> failure(): CPSResult<A> = result { k -> { } }

fun <A> memo_result(res: () -> CPSResult<A>): CPSResult<A> {
    val Ks: Deque<K<A>> = ArrayDeque<K<A>>()
    var Rs: Set<A> = LinkedHashSet<A>()
    return object : CPSResult<A>() {
        override fun invoke(k: K<A>) {
            if (Ks.isEmpty()) {
                Ks.push(k)
                val ki: K<A> = { t ->
                    if (!Rs.contains(t)) {
                        Rs += t
                        val iter = Ks.iterator()
                        while(iter.hasNext()) Trampoline.call(iter.next(), t)
                    }
                }
                res()(ki)
            } else {
                Ks.push(k)
                val iter = Rs.iterator()
                while(iter.hasNext()) Trampoline.call(k, iter.next())
            }
        }
    }
}

fun <A> memo(f: Recognizer<A>): Recognizer<A> = Memo(f)

class Memo<A>(val f: Recognizer<A>): Recognizer<A>() {
    val table: MutableMap<A, CPSResult<A>> = HashMap()
    override fun invoke(p: A): CPSResult<A> = table.getOrPut(p) { memo_result { f(p) } }
    override fun init(s: String) {
        super.init(s)
        f.init(s)
    }
}

fun <A, B> (CPSResult<A>).map(f: (A) -> B): CPSResult<B> =
        result { k -> this(memo_k { t -> k(f(t)) }) }

fun <A, B> (CPSResult<A>).flatMap(f: (A) -> CPSResult<B>): CPSResult<B> =
        result { k -> this(memo_k { t -> f(t)(k) })}

fun <A> (CPSResult<A>).orElse(rhs: () -> CPSResult<A>): CPSResult<A> =
        result { k -> Trampoline.alt(this, k, rhs) }

fun <A> epsilon(): Recognizer<Int> = parser { i -> success(i) }

fun <A> seq(r1: Recognizer<A>, r2: Recognizer<A>): Recognizer<A> = SeqP(r1,r2)

class SeqP<A>(val r1: Recognizer<A>, val r2: Recognizer<A>): Recognizer<A>() {
    override fun invoke(p: A): CPSResult<A> {
        return r1(p).flatMap(r2)
    }

    override fun init(s: String) {
        super.init(s)
        r1.init(s)
        r2.init(s)
    }
}

fun <A> rule(r1: Recognizer<A>, r2: Recognizer<A>): Recognizer<A> = memo(DisjP(r1, r2))

class DisjP<A>(val r1: Recognizer<A>, val r2: Recognizer<A>): Recognizer<A>() {

    override fun invoke(p: A): CPSResult<A> = r1(p).orElse{ r2(p) }
    override fun init(s: String) {
        super.init(s)
        r1.init(s)
        r2.init(s)
    }
}

class ProxyParserNotSetException(): Exception()
class ProxyRecognizer<A> (var recognizer: Recognizer<A>?) : Recognizer<A>() {
    override fun invoke(p: A): CPSResult<A>  {
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

class And <A> (val p1: Recognizer<A>, val p2: Recognizer<A>) : Recognizer<A>() {
    override fun invoke(p: A): CPSResult<A> {
        val passedByp1: MutableSet<A> = TreeSet()
        val passedByp2: MutableSet<A> = TreeSet()
        val executed: MutableSet<A> = TreeSet()

        return object : CPSResult<A>() {
            override fun invoke(k: K<A>) {
                    p1(p)({ res: A ->
                        passedByp1.add(res)
                        if (passedByp2.contains(res) && !executed.contains(res)) {
                            k(res)
                            executed.add(res)
                        }
                    })

                    p2(p)({ res  ->
                        passedByp2.add(res)
                        if (passedByp1.contains(res) && !executed.contains(res)) {
                            k(res)
                        }
                    })
            }
        }
    }
    override fun init(s: String) {
        super.init(s)
        p1.init(s)
        p2.init(s)
    }
}

fun <A> and(p1: Recognizer<A>, p2: Recognizer<A>): Recognizer<A> = And(p1, p2)

fun terminal(t: String): Recognizer<Int> = parser {
    i -> if (input!!.startsWith(t, i)) success(i + 1) else failure()
}

fun satp(cond: (Char) -> Boolean): Recognizer<Int> = parser { i ->
    if (cond(input!![i])) {
        return@parser success(i + 1)
    }
    failure<Int>()
}

val digit: Recognizer<Int> = satp { ('0'..'9').contains(it) }
val alpha: Recognizer<Int> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
val alphaOrDigit: Recognizer<Int> = alpha / digit

class Call<A>(val k: (A) -> Unit, val t: A): Runnable {
    override fun run(): Unit = k(t)
}
class Seq<A>(val r: () -> (((A) -> Unit) -> Unit), val k: (A) -> Unit): Runnable {
    override fun run(): Unit = r()(k)
}
class Alt<A>(val lhs: ((A) -> Unit) -> Unit, val k: (A) -> Unit,
             val rhs: () -> ((A) -> Unit) -> Unit): Runnable {
    override fun run(): Unit {
        Trampoline.jobs.push(Seq(rhs, k)); lhs(k)
    }
}

object Trampoline {
    var jobs: Deque<Runnable> = ArrayDeque<Runnable>()
    fun <A> call(k: (A) -> Unit, t: A): Unit = Trampoline.jobs.push(Call(k, t))
    fun <A> alt(lhs: ((A) -> Unit) -> Unit, k: (A) -> Unit,
                rhs: () -> ((A) -> Unit) -> Unit): Unit =
            Trampoline.jobs.push(Alt(lhs, k, rhs))
    fun run(): Unit {
        while(!jobs.isEmpty()) jobs.pop().run()
    }
}