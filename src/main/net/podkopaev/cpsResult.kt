package net.podkopaev.cpsResult

import java.util.*

abstract class Result <A> {
    abstract operator fun  invoke(k: (A) -> Unit)
}

abstract class CPSResult<A> {
    fun <A> result(f: ((A) -> Unit) -> Unit) =
            object : Result<A>() {
                override fun invoke(k: (A) -> Unit) = f(k)
            }

    fun <A> success(t: A): Result<A> = result { k -> k(t) }
    fun <A> failure(): Result<A> = result { k -> { } }

    fun <A> memo_k(f: (A) -> Unit): (A) -> Unit {
        val s: HashSet<A> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    fun <B> map(f: (A) -> B): Result<B> = result { k ->
        this(memo_k{ t -> k(f(t)) }) }
    fun <B> (Result<Int>).flatMap(f: (Int) -> Result<B>): Result<B> =
            result{ k -> this(memo_k { t -> f(t)(k) })}
    fun <A> (Result<Int>).orElse(r: ((Int) -> Unit) -> Result<A>) = {
        lazy { val v = r; result { k: (Int) -> Unit -> this(k); v(k) } }
    }

    abstract operator fun  invoke(k: (A) -> Unit)
}

abstract class MemoizedCPS<A>: MemoizedCPSResult<A>() {
    fun <A> memo(f: (Int) -> Result<A>): (Int) -> Result<A>? {
        val table: MutableMap<Int, Result<A>> = HashMap()
        return { i: Int -> table.getOrElse(i) { val v: Result<A> = memo_result({f(i)}); table.put(i, v) } }
    }
}

abstract class MemoizedCPSResult<A> : CPSResult<A>() {
    fun <A> memo_result(res: ((A) -> Unit) -> Result<A>): Result<A> {
        val Rs: MutableList<A> = ArrayList()
        val Ks: MutableList<(A) -> Unit> = ArrayList()
        return result { k ->
            if (Ks.isEmpty()) {
                Ks += k
                val ki: (A) -> Unit = { t ->
                    if (!Rs.contains(t)) {
                        Rs += t; for (kt in Ks) kt(t)
                    }
                }
                res(ki)
            } else {
                Ks += k
                for (t in Rs) k(t)
            }
        }
    }
}

abstract class Recognizers<A>() : CPSResult<A>() {
    var input: String? = null

    fun terminal(t: String): (Int) -> Result<Int> = {
        i -> if(input!!.startsWith(t, i)) success(i + t.length)
        else failure()
    }

    fun epsilon(): (Int) -> Result<Int> = { i -> success(i) }

    fun seq(r1: (Int) -> Result<Int>, r2: (Int) -> Result<Int>): (Int) -> Result<Int> = {
        i -> r1(i).flatMap({ r2(i) })
    }

    fun alt(r1: (Int) -> Result<Int>, r2: (Int) -> Result<Int>) = {
        i: Int -> r1(i).orElse{ r2(i) }
    }
}
