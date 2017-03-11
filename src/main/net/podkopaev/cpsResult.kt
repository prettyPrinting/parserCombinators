package net.podkopaev.cpsResult

import java.util.*

typealias K<A> = (A) -> Unit
typealias Recognizer = (Int) -> CPSResult<Int>

abstract class CPSResult<A>: (((A) -> Unit) -> Unit) {
    fun <A> result(f: (K<A>) -> Unit): CPSResult<A> =
            object : CPSResult<A>() {
                override fun invoke(k: K<A>) = f(k)
            }

    fun <A> success(t: A): CPSResult<A> = result { k -> k(t) }
    fun <A> failure(): CPSResult<A> = result { k -> { } }

    fun <A> memo_k(f: K<A>): K<A> {
        val s: HashSet<A> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    fun <B> (CPSResult<Int>).map(f: (Int) -> B): CPSResult<B> = result { k ->
        this(memo_k { t -> k(f(t)) }) }

    fun <B> (CPSResult<Int>).flatMap(f: (Int) -> CPSResult<B>): CPSResult<B> =
            result{ k -> this(memo_k { t -> f(t)(k) })}

    fun <A> (CPSResult<A>).orElse(rhs: () -> CPSResult<A>): CPSResult<A> = result {
        k -> Trampoline.alt(this, k, rhs)
    }

    override abstract operator fun  invoke(k: K<A>)
}

abstract class MemoizedCPS<A>: MemoizedCPSResult<A>() {
    fun <A> memo(f: (Int) -> CPSResult<A>): (Int) -> CPSResult<A>? {
        val table: MutableMap<Int, CPSResult<A>> = HashMap()
        return { i: Int -> table.getOrElse(i) { val v: CPSResult<A> = memo_result({f(i)}); table.put(i, v) } }
    }
}

abstract class MemoizedCPSResult<A> : CPSResult<A>() {
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
class Seq<A>(val r: () -> (((A) -> Unit) -> Unit), val k: (A) -> Unit): Runnable() {
    override fun run(): Unit = r()(k)
}
class Alt<A>(val lhs: ((A) -> Unit) -> Unit, val k: (A) -> Unit,
             val rhs: () -> ((A) -> Unit) -> Unit): Runnable() {
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