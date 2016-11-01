package net.podkopaev

import java.util.*

class Result<A>(
        private val list: List<Pair<A, String>>
): Iterable<Pair<A, String>> {
    operator fun plus(right: Result<A>): Result<A> =
            Result(this.list + right.list)

    override fun iterator(): Iterator<Pair<A, String>> = list.iterator()
    fun empty(): Boolean = list.isEmpty()

    // Possible dangerous.
    fun get(): Pair<A, String> = list[0]
}

interface Parser<A> {
    operator fun invoke(input: String): Result<A>

    operator fun <B> times(right: (A) -> Parser<B>): Parser<B> =
            bindp(this, right)
    operator fun <B> plus(right: (A) -> B): Parser<B> =
            transp(this, right)
    operator fun <B> minus(right: Parser<B>): Parser<B> =
            seqrp(this, right)
    operator fun div(right: Parser<A>): Parser<A> =
            disjp(this, right)

    fun get(input: String): A? {
        val results = this(input)
        for (r in results) {
            if (!r.second.equals("")) { continue }
            return r.first
        }
        return null
    }
}

class Top private constructor () {
    companion object {
        val top: Top = Top()
    }
}

fun <A> parser(parse: Parser<A>.(String) -> Result<A>): Parser<A> =
        object : Parser<A> {
            override fun invoke(input: String): Result<A> = parse(input)
        }

fun <A> conp(value: A): Parser<A> = parser { input ->
    Result(listOf(Pair(value, input)))
}

fun litp(str: String): Parser<String> = parser { input ->
    if (!input.startsWith(str)) {
        return@parser Result(listOf())
    }
    val resultPair = Pair(str, input.drop(str.length))
    return@parser Result(listOf(resultPair))
}

fun <A, B> transp(parser: Parser<A>, f: (A) -> B): Parser<B> =
        bindp(parser) { conp(f(it)) }

fun <A> disjp(left: Parser<A>, right: Parser<A>): Parser<A> = parser { input ->
    left(input) + right(input)
}

fun <A, B> seqlp(left: Parser<A>, right: Parser<B>): Parser<A> =
        combinep(left, right) { l, r -> l }

fun <A, B> seqrp(left: Parser<A>, right: Parser<B>): Parser<B> =
        bindp(left) { right }

fun <A, B> bindp(left: Parser<A>, right: (A) -> Parser<B>): Parser<B> = parser { input ->
    val leftResult = left(input)
    val resultList = LinkedList<Result<B>>()
    for (p in leftResult) {
        val rParser = right(p.first)
        val cp = rParser(p.second)
        resultList.add(cp)
    }
    return@parser Result(resultList.flatten())
}

fun satp(cond: (Char) -> Boolean): Parser<Char> = parser { input ->
    if (input.length < 1 || !cond(input[0])) {
        return@parser Result(listOf())
    }
    return@parser Result(listOf(Pair(input[0], input.drop(1))))
}

fun <A, B, C> combinep(left: Parser<A>, right: Parser<B>, f: (A, B) -> C): Parser<C> = parser { input ->
    val leftResult = left(input)
    val resultList = LinkedList<List<Pair<C, String>>>()
    for (p in leftResult) {
        val rightResult = right(p.second)
        resultList.add(rightResult.map {
            Pair(f(p.first, it.first), it.second)
        })
    }
    return@parser Result(resultList.flatten())
}

fun <A> combineListp(left: Parser<A>, right: Parser<List<A>>): Parser<List<A>> =
        combinep(left, right) { x, xs -> listOf(x) + xs }

fun <A, B, C, D> combine3p(fst: Parser<A>, snd: Parser<B>, trd: Parser<C>, f: (A, B, C) -> D): Parser<D> =
        combinep(combinep(fst, snd) { x, y -> Pair(x, y) }, trd) { p, z -> f(p.first, p.second, z) }

val empty: Parser<Top> = parser { input ->
    Result(listOf(Pair(Top.top, input)))
}

//fun <A> many0(p: Parser<A>): Parser<List<A>> = many1(p) / (empty + { listOf<A>() })
//fun <A> many1(p: Parser<A>): Parser<List<A>> = parser { input ->
//    val parser: Parser<List<A>> = combineListp(p, many0(p))
//    parser(input)
//}
fun <A> many0(p: Parser<A>): Parser<List<A>> = parser { input ->
    var stackToWork: Stack<Pair<MutableList<A>, String>> = Stack()
    val finalResult: LinkedList<Pair<List<A>, String>> = LinkedList()

    stackToWork.push(Pair(LinkedList(), input))
    while (!stackToWork.empty()) {
        val curTask = stackToWork.pop()
        val curList = curTask.first
        val curString = curTask.second
        val nextPParse = p(curString)
        if (nextPParse.empty()) {
            finalResult.push(curTask)
            continue
        }

        // Cheating: only one result of parsing by p.
        val pResult = nextPParse.get()
        curList.add(pResult.first)
        stackToWork.push(Pair(curList, pResult.second))
    }

    return@parser Result(finalResult)
}
fun <A> many1(p: Parser<A>): Parser<List<A>> = combineListp(p, many0(p))

fun char(c: Char): Parser<Char> = satp { it.equals(c) }
val digit: Parser<Char> = satp { ('0'..'9').contains(it) }
val alpha: Parser<Char> = satp {
    ('a'..'z').contains(it) || ('A'..'Z').contains(it)
}
val alphaOrDigit: Parser<Char> = alpha / digit

fun List<Char>.toStr(): String = String(toCharArray())
val symbol: Parser<String> =
        (combineListp(alpha, many0(alphaOrDigit))) + { it.toStr() }
val number: Parser<Int> = many1(digit) + { it.toStr().toInt() }
val word  : Parser<String> = many1(alpha) + { it.toStr() }

fun <A, B, C> gparen(leftparen: Parser<A>, p: Parser<B>, rightparen: Parser<C>): Parser<B> =
        leftparen - seqlp(p, rightparen)
fun <A> paren(p: Parser<A>): Parser<A> = gparen(litp("("), p, litp(")"))
fun <A> cparen(p: Parser<A>): Parser<A> = gparen(litp("{"), p, litp("}"))

val space : Parser<Char> =
        char(' ') / char('\n') / char('\t') /
                (litp("\r\n") + { '\n' })
val spaces: Parser<String> = many0(space) + { it.toStr() }
fun <A> sp(p: Parser<A>): Parser<A> = gparen(spaces, p, spaces)

fun <A> leftAssocp(opp: Parser<String>, elemp: Parser<A>, f: (String, A, A) -> A): Parser<A> = parser { input ->
    val rightp: Parser<Pair<String, A>> =
            combinep(opp, elemp) { op, e -> Pair(op, e) }
    val rightLp: Parser<List<Pair<String, A>>> = many0(rightp)
    val parser = combinep(elemp, rightLp) { e, l ->
        l.fold(e){ e, t -> f(t.first, e, t.second) }
    }
    parser(input)
}