package com.math.autotapper2

/**
 * Evaluates expressions containing integers and + - * / with correct precedence.
 * Supports symbols: x (multiply) and ÷ (divide) by mapping to * and /.
 */
object MathEngine {
    fun evaluate(expr: String): Double {
        val s = expr.replace("x", "*").replace("÷", "/")
        return evaluateBasic(s)
    }

    private fun evaluateBasic(expr: String): Double {
        // Shunting-yard to RPN (Reverse Polish Notation)
        val output = mutableListOf<String>()
        val ops = ArrayDeque<Char>()

        fun prec(c: Char) = when (c) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> -1
        }

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> { /* skip */ }
                c.isDigit() -> {
                    val sb = StringBuilder()
                    var k = i
                    while (k < expr.length && expr[k].isDigit()) {
                        sb.append(expr[k]); k++
                    }
                    output.add(sb.toString())
                    i = k - 1
                }
                c in charArrayOf('+','-','*','/') -> {
                    while (ops.isNotEmpty() && prec(ops.last()) >= prec(c)) {
                        output.add(ops.removeLast().toString())
                    }
                    ops.addLast(c)
                }
                else -> {
                    // ignore unsupported chars safely
                }
            }
            i++
        }
        while (ops.isNotEmpty()) output.add(ops.removeLast().toString())

        // Evaluate RPN
        val st = ArrayDeque<Double>()
        for (tok in output) {
            if (tok.length > 1 || tok[0].isDigit()) {
                st.addLast(tok.toDouble())
            } else {
                val b = st.removeLast()
                val a = st.removeLast()
                val r = when (tok[0]) {
                    '+' -> a + b
                    '-' -> a - b
                    '*' -> a * b
                    '/' -> a / b
                    else -> 0.0
                }
                st.addLast(r)
            }
        }
        return st.removeLast()
    }
}
