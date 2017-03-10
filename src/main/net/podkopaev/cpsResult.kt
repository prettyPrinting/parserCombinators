package net.podkopaev.cpsResult

import java.util.*

typealias K<A> = (A) -> Unit
typealias Recognizer = (Int) -> Result<Int>

abstract class Result<A> {
    abstract operator fun  invoke(k: K<A>)
}

abstract class CPSResult<A> {
    fun <A> result(f: (K<A>) -> Unit) =
            object : Result<A>() {
                override fun invoke(k: K<A>) = f(k)
            }

    fun <A> success(t: A): Result<A> = result { k -> k(t) }
    fun <A> failure(): Result<A> = result { k -> { } }

    fun <A> memo_k(f: K<A>): K<A> {
        val s: HashSet<A> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    fun <B> (Result<Int>).map(f: (Int) -> B): Result<B> = result { k ->
        this(memo_k { t -> k(f(t)) }) }

    fun <B> (Result<Int>).flatMap(f: (Int) -> Result<B>): Result<B> =
            result{ k -> this(memo_k { t -> f(t)(k) })}

    fun <A> (Result<A>).orElse(r: (Unit) -> Result<A>) = {
        lazy { val v = r; result { k: K<A> -> this(k); v(Unit)(k) } }
    }

    abstract operator fun  invoke(k: K<A>)
}

abstract class MemoizedCPS<A>: MemoizedCPSResult<A>() {
    fun <A> memo(f: (Int) -> Result<A>): (Int) -> Result<A>? {
        val table: MutableMap<Int, Result<A>> = HashMap()
        return { i: Int -> table.getOrElse(i) { val v: Result<A> = memo_result({f(i)}); table.put(i, v) } }
    }
}

abstract class MemoizedCPSResult<A> : CPSResult<A>() {
    fun <A> memo_result(res: (Unit) -> Result<A>): Result<A> {
        val Rs: MutableList<A> = ArrayList()
        val Ks: MutableList<K<A>> = ArrayList()
        return result { k ->
            if (Ks.isEmpty()) {
                Ks += k
                val ki: K<A> = { t ->
                    if (!Rs.contains(t)) {
                        Rs += t; for (kt in Ks) kt(t)
                    }
                }
                val r = ki
                res(Unit)(ki)
            } else {
                Ks += k
                for (t in Rs) k(t)
            }
        }
    }
}

abstract class Recognizers<A> : CPSResult<A>() {
    var input: String? = null

    fun terminal(t: String): Recognizer = {
        i -> if(input!!.startsWith(t, i)) success(i + t.length)
        else failure()
    }

    fun epsilon(): Recognizer = { i -> success(i) }

    fun seq(r1: Recognizer, r2: Recognizer): Recognizer = {
        i -> r1(i).flatMap(r2)
    }

    fun alt(r1: Recognizer, r2: Recognizer) = {
        i: Int -> r1(i).orElse{ r2(i) }
    }
    internal open fun init(s: String) {
        input = s
    }
}

abstract class Runnable {
    abstract fun run(): Unit
}
class Call<A>(val k: (A) -> Unit, val t: A): Runnable() {
    override fun run(): Unit = k(t)
}
class Seq<A>(val r: (Unit) -> (((A) -> Unit) -> Unit), val k: (A) -> Unit): Runnable() {
    override fun run(): Unit = r(Unit)(k)
}
class Alt<A>(val lhs: ((A) -> Unit) -> Unit, val k: (A) -> Unit,
             val rhs: (Unit) -> ((A) -> Unit) -> Unit): Runnable() {
    override fun run(): Unit {
        Trampoline.jobs.push(Seq(rhs, k)); lhs(k)
    }
}
object Trampoline {
    var jobs: Deque<Runnable> = ArrayDeque<Runnable>()
    fun <A> call(k: (A) -> Unit, t: A): Unit = Trampoline.jobs.push(Call(k, t))
    fun <A> alt(lhs: ((A) -> Unit) -> Unit, k: (A) -> Unit,
                rhs: (Unit) -> ((A) -> Unit) -> Unit): Unit =
            Trampoline.jobs.push(Alt(lhs, k, rhs))
    fun run(): Unit {
        while(!jobs.isEmpty()) jobs.pop().run()
    }
}