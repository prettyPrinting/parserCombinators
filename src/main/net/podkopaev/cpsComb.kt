package net.podkopaev.cpsComb

import java.util.*

typealias K<A> = (A) -> Unit
//typealias Recognizer = (Int) -> CPSResult<Int>

abstract class Recognizer: (Int) -> CPSResult<Int> {
    var Ks: ArrayList<K<Int>> = ArrayList()
    override abstract operator fun  invoke(k: Int): CPSResult<Int>
}

fun parser(r: (Int) -> CPSResult<Int>): Recognizer =
        object : Recognizer() {
            override fun invoke(p: Int): CPSResult<Int> = r(p)
        }

abstract class CPSResult<A>: (K<A>) -> Unit {
    fun memo_k(f: K<Int>): K<Int> {
        val s: HashSet<Int> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    override abstract operator fun  invoke(k: K<A>)
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

fun  memo(f: (Int) -> CPSResult<Int>): Recognizer {
    val table: MutableMap<Int, CPSResult<Int>> = HashMap()
    return parser{ i: Int -> table.getOrPut(i) { memo_result { f(i) } }}
}

fun <B> (CPSResult<Int>).map(f: (Int) -> B): CPSResult<B> =
        result { k -> this(memo_k { t -> k(f(t)) }) }

fun <B> (CPSResult<Int>).flatMap(f: (Int) -> CPSResult<B>): CPSResult<B> =
        result { k -> this(memo_k { t -> f(t)(k) })}

fun <A> (CPSResult<A>).orElse(rhs: () -> CPSResult<A>): CPSResult<A> =
        result { k -> Trampoline.alt(this, k, rhs) }

abstract class Recognizers<A> : CPSResult<A>() {
    var input: String? = null

    operator fun (Recognizer).div (p: Recognizer): Recognizer = rule (this, p)

    fun terminal(t: String): Recognizer = parser{
        i -> if(input!!.startsWith(t, i)) success(i + t.length)
        else failure()
    }

    fun epsilon(): Recognizer = parser{ i -> success(i) }

    fun seq(r1: Recognizer, r2: Recognizer): Recognizer = parser{
        i -> r1(i).flatMap(r2)
    }

    fun rule(r1: Recognizer, r2: Recognizer): Recognizer = memo(
            { i: Int -> r1(i).orElse{ r2(i) } }
    )

    internal open fun init(s: String) {
        input = s
    }
}

class ProxyParserNotSetException(): Exception()
class ProxyRecognizer(var recognizer: Recognizer?) : Recognizer() {
    override fun invoke(p1: Int): CPSResult<Int>  {
        return recognizer?.invoke(p1) ?: throw ProxyParserNotSetException()
    }
}

val plain_fix = { f : (Recognizer) -> Recognizer ->
    val proxy = ProxyRecognizer(null)
    val result = f (proxy)
    proxy.recognizer = result
    result
}

val fix = { f: (Recognizer) -> Recognizer ->
    val mf = { p : Recognizer -> memo(f(p)) }
    plain_fix(mf)
}

class And() {
    var passedByp1: ArrayList<Int> = ArrayList()
    var passedByp2: ArrayList<Int> = ArrayList()
    constructor(p1: Recognizer, p2: Recognizer) : this() {
        p1.Ks.add {
            i -> passedByp1.add(i); if (passedByp2.contains(i)) success(i)
        }
        p2.Ks.add {
            i -> passedByp2.add(i); if (passedByp1.contains(i)) success(i)
        }
    }
}

fun and(p1: Recognizer, p2: Recognizer) = And(p1, p2)

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