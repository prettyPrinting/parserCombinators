package net.podkopaev.cpsComb

import java.util.*

typealias K<A> = (A) -> Unit

abstract class Recognizer<A>: (A) -> CPSResult<A> {
    override abstract operator fun  invoke(p: A): CPSResult<A>
}

fun <A> parser(r: (A) -> CPSResult<A>): Recognizer<A> =
        object : Recognizer<A>() {
            override fun invoke(p: A): CPSResult<A> = r(p)
        }

abstract class CPSResult<A>: (K<A>) -> Unit {
    fun memo_k(f: K<A>): K<A> {
        val s: HashSet<A> = HashSet()
        return { t -> if(!s.contains(t)) { s += t; f(t) } }
    }

    override abstract operator fun  invoke(k: K<A>) : Unit
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

fun <A> memo(f: (A) -> CPSResult<A>): Recognizer<A> {
    val table: MutableMap<A, CPSResult<A>> = HashMap()
    return parser{ i: A -> table.getOrPut(i) { memo_result { f(i) } }}
}

fun <A, B> (CPSResult<A>).map(f: (A) -> B): CPSResult<B> =
        result { k -> this(memo_k { t -> k(f(t)) }) }

fun <A, B> (CPSResult<A>).flatMap(f: (A) -> CPSResult<B>): CPSResult<B> =
        result { k -> this(memo_k { t -> f(t)(k) })}

fun <A> (CPSResult<A>).orElse(rhs: () -> CPSResult<A>): CPSResult<A> =
        result { k -> Trampoline.alt(this, k, rhs) }

abstract class Recognizers<A> : CPSResult<A>() {
    var input: String? = null

    operator fun (Recognizer<A>).div (p: Recognizer<A>): Recognizer<A> = rule (this, p)

    fun terminal(t: String): Recognizer<Int> = parser{
        i -> if(input!!.startsWith(t, i)) success(i + t.length)
        else failure()
    }

    fun epsilon(): Recognizer<Int> = parser { i -> success(i) }

    fun seq(r1: Recognizer<A>, r2: Recognizer<A>): Recognizer<A> = parser {
        i -> r1(i).flatMap(r2)
    }

    fun rule(r1: Recognizer<A>, r2: Recognizer<A>): Recognizer<A> = memo(
            { i: A -> r1(i).orElse{ r2(i) } }
    )

    internal open fun init(s: String) {
        input = s
    }
}

class ProxyParserNotSetException(): Exception()
class ProxyRecognizer<A> (var recognizer: Recognizer<A>?) : Recognizer<A>() {
    override fun invoke(p: A): CPSResult<A>  {
        return recognizer?.invoke(p) ?: throw ProxyParserNotSetException()
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
}

fun <A> and(p1: Recognizer<A>, p2: Recognizer<A>): Recognizer<A> = And(p1, p2)

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