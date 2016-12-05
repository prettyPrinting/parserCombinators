package net.podkopaev.cpsComb

import java.util.*

abstract class MatchFail<A>() {
    abstract fun match(startPos: Int, endPos: Int, result: A)
    abstract fun fail (startPos: Int)

    abstract fun init(s: String)
}

abstract class Parser<A>() {
    public val klist: ArrayList<MatchFail<A>> = ArrayList()
    public var parseString: String? = null

    internal open fun init(s: String) {
        parseString = s
    }

    abstract operator fun invoke(pos: Int)
}

class MatchFailMap<A>(): MatchFail<A>() {
    public var resultMap : HashMap<Int, Pair<Int, A>> = HashMap()

    override fun match(startPos : Int, endPos : Int, result : A) {
        if (startPos in resultMap) { return }
        resultMap.put(startPos, Pair(endPos, result))
    }
    override fun fail (startPos : Int) {}

    override fun init(s: String) {
        resultMap = HashMap()
    }
}

fun <A> parser(invokeBody : Parser<A>.(Int) -> Unit): Parser<A> =
        object : Parser<A>() {
            override fun invoke(pos: Int) = invokeBody(pos)
        }

fun <A> conp(value: A): Parser<A> = parser { pos ->
    for (k in klist) {
        k.match(pos, pos, value)
    }
}

fun litp(str: String): Parser<String> = parser { pos ->
    val substring = parseString!!.substring(pos)
    if (!substring.startsWith(str)) {
        for (k in klist) {
            k.fail(pos)
        }
        return@parser
    }
    for (k in klist) {
        k.match(pos, pos + str.length, str)
    }
}

/*
fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> = parser { pos ->
    parser.klist.add(
            object: MatchFail<A>() {
                override fun match(startPos: Int, endPos: Int, result: A) {
                    this.match(startPos, endPos, f(result))
                }

                override fun fail(startPos: Int) {
                    this.fail(startPos)
                }

                override fun init(s: String) {
                    this.init(s)
                }
            }
    )
    parser(pos)
}
*/
