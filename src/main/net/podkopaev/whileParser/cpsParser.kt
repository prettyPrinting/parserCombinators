package net.podkopaev.cpsParser

import net.podkopaev.cpsComb.*
import net.podkopaev.whileParser.Expr
import net.podkopaev.whileParser.Stmt

fun generateExprParser(): Recognizer<Expr> = fix {
    val corep = (number map { Expr.Con(it) as Expr }) /
                (symbol map { Expr.Var(it) as Expr }) /
                 paren( sp(it) )
    val op1p = rightAssocp(sp(terminal("^")), corep) { l, op, r -> Expr.Binop(l, op, r) }
    val op2p = rightAssocp(sp(terminal("*") / terminal("/") / terminal("%")), op1p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
    val op3p = assocp (sp(terminal("+") / terminal("-")), op2p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
    return@fix op3p
}
val exprParser: Recognizer<Expr> = generateExprParser()

fun generateStmtParser(): Recognizer<Stmt> = fix {
    val readp  : Recognizer<Stmt> = terminal("read" ) seqr spaces seqr paren(sp(symbol))     map { Stmt.Read (it) as Stmt }
    val writep : Recognizer<Stmt> = terminal("write") seqr spaces seqr paren(sp(exprParser)) map { Stmt.Write(it) as Stmt }
    val assignp: Recognizer<Stmt> = ((symbol seql spaces seql terminal(":=") seql spaces) + exprParser) map { nameexpr ->
            Stmt.Assign(nameexpr.first, nameexpr.second) as Stmt
        }
    val ifp = (terminal("if"  ) seqr spaces seqr paren(sp(exprParser)) seql spaces) +
              (terminal("then") seqr spaces seqr it         seql spaces) +
              (terminal("else") seqr spaces seqr it         seql spaces) - terminal("fi") map {
                ete -> Stmt.If(ete.first.first, ete.first.second, ete.second) as Stmt
            }
    val whilep = (terminal("while") seqr spaces seqr        exprParser seql spaces) +
                 (terminal("do"   ) seqr spaces seqr        it         seql spaces) -
                  terminal("od") map {
                eb -> Stmt.While(eb.first, eb.second) as Stmt
            }
    val corepp: Recognizer<Stmt> =  readp / writep / assignp / ifp / whilep
    val parser = assocp(terminal(";"), corepp) {
        op, s1, s2 ->
        Stmt.Seq(s1, s2)
    }
    return@fix parser
}
val stmtParser = generateStmtParser()

fun <A> assocp(opp: Recognizer<String>, elemp: Recognizer<A>,
               f: (String, A, A) -> A): Recognizer<A> = fix { P ->
    elemp / ((elemp + opp + P) map { f(it.first.second, it.first.first, it.second) })
}