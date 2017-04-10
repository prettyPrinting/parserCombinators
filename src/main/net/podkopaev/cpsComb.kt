package net.podkopaev.cpsComb

import java.util.*

class ProxyParserNotSetException(): Exception()

typealias K<A> = (A) -> Unit
typealias Recognizer = (Int) -> CPSResult<Int>

abstract class CPSResult<A>: (K<A>) -> Unit {
    fun <A> result(f: (K<A>) -> Unit): CPSResult<A> =
            object : CPSResult<A>() {
                override fun invoke(k: K<A>) = f(k)
            }

    fun <A> success(t: A): CPSResult<A> = result { k -> k(t) }
    fun <A> failure(): CPSResult<A> = result { k -> { } }

    fun memo_k(f: K<Int>): K<Int> {
        val s: HashSet<Int> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    override abstract operator fun  invoke(k: K<A>)
}

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

fun  memo(f: (Int) -> CPSResult<Int>): (Int) -> CPSResult<Int> {
    val table: MutableMap<Int, CPSResult<Int>> = HashMap()
    return { i: Int -> table.getOrPut(i) { memo_result { f(i) } }}
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

    fun terminal(t: String): Recognizer = {
        i -> if(input!!.startsWith(t, i)) success(i + t.length)
        else failure()
    }

    fun epsilon(): Recognizer = { i -> success(i) }

    fun seq(r1: Recognizer, r2: Recognizer): Recognizer = {
        i -> r1(i).flatMap(r2)
    }

    fun rule(r1: Recognizer, r2: Recognizer): Recognizer = memo(
            { i: Int -> r1(i).orElse{ r2(i) } }
    )

    internal open fun init(s: String) {
        input = s
    }
}

class ProxyRecognizer(var recognizer: Recognizer?) : Recognizer {
    override fun invoke(p1: Int): CPSResult<Int>  {
        val recognizer = recognizer
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