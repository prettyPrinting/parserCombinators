package net.podkopaev.cpsParser

import net.podkopaev.cpsComb.*
import net.podkopaev.whileParser.Expr
import net.podkopaev.whileParser.Stmt

val corep: Recognizer<Expr> = fix { (number map { Expr.Con(it) as Expr }) /
        (symbol map { Expr.Var(it) as Expr }) /
        paren( sp(it) ) }

val op1p: Recognizer<Expr> = fix { rightAssocp(sp(terminal("^")), corep) { l, op, r -> Expr.Binop(l, op, r) }}

val op2p: Recognizer<Expr> = fix {
    rightAssocp(sp(terminal("*") / terminal("/") / terminal("%")), op1p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
}

val op3p: Recognizer<Expr> = fix {
    leftAssocp(sp(terminal("+") / terminal("-")), op2p) {
        op, e1, e2 ->
        Expr.Binop(op, e1, e2)
    }
}

val exprParser: Recognizer<Expr> = fix { corep / op1p / op3p / op2p }

val readp: Recognizer<Stmt>   = fix { terminal("read" ) seqr spaces seqr paren(sp(symbol))     map { Stmt.Read (it) as Stmt } }
val writep: Recognizer<Stmt>  = fix { terminal("write") seqr spaces seqr paren(sp(exprParser)) map { Stmt.Write(it) as Stmt } }
val assignp: Recognizer<Stmt> = fix {
    ((symbol seql spaces seql terminal(":=") seql spaces) + exprParser) map { nameexpr ->
        Stmt.Assign(nameexpr.first, nameexpr.second) as Stmt
    }
}

val corepp: Recognizer<Stmt> =  writep / assignp / readp

val stmtParser: Recognizer<Stmt> =
    rightAssocpTest(terminal(";"), corepp) {
        op, s1, s2 -> Stmt.Seq(s1, s2)
    }
fun <A> rightAssocpTest(opp: Recognizer<String>, elemp: Recognizer<A>,
                    f: (String, A, A) -> A): Recognizer<A> = fix {
    elemp / ((elemp + opp + elemp) map { f(it.first.second, it.first.first, it.second) })
}