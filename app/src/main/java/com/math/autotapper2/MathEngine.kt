
package com.math.autotapper2

object MathEngine {
    // Simple expression evaluator supporting + - * / with proper precedence.
    fun evaluate(expr: String): Double {
        val s = expr.replace("x", "*").replace("÷", "/")
        return evaluateBasic(s)
    }

    private fun evaluateBasic(expr: String): Double {
        // Shunting-yard to RPN
        val output = ArrayDeque<String>()
        val ops = ArrayDeque<Char>()
        fun prec(c: Char) = when (c) { '+','-' -> 1; '*','/' -> 2; else -> -1 }

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit()) {
                val sb = StringBuilder()
                var k = i
                while (k < expr.length && expr[k].isDigit()) { sb.append(expr[k]); k++ }
                output.addLast(sb.toString())
                i = k - 1
            } else if (c in charArrayOf('+','-','*','/')) {
                while (ops.isNotEmpty() && prec(ops.peek()) >= prec(c)) {
                    output.addLast(ops.pop().toString())
                }
                ops.push(c)
            }
            i++
        }
        while (ops.isNotEmpty()) output.addLast(ops.pop().toString())

        val st = ArrayDeque<Double>()

        for (tok in output) {
            if (tok.length > 1 || tok[0].isDigit()) {
                st.push(tok.toDouble())
            } else {
                val b = st.pop()
                val a = st.pop()
                val r = when (tok[0]) {
                    '+' -> a + b
                    '-' -> a - b
                    '*' -> a * b
                    '/' -> a / b
                    else -> 0.0
                }
                st.push(r)
            }
        }
        return st.pop()
    }
}
