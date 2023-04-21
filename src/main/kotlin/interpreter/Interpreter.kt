package interpreter

import java.io.File

val operators = arrayOf("+", "-", "*", "/", "^", "%")
val conditions = arrayOf("&&", "||", "!", "??")
val assignments = arrayOf("=")
val comments = arrayOf("//")
val keywords = arrayOf("if")

val symbols = arrayOf('+', '-', '*', '/', '^', '%', '&', '|', '!', '?', '=')

class Interpreter(private val file: File, private val debug: Boolean) {
    private var data: DataArray = arrayOf()

    fun interpret() {
        val lines = this.file.readText().split(Regex("[\n;]")).map { it.trim() }

        var lineIndex = 1
        lines.forEach line@{ line: String ->
            if (line.isEmpty()) return@line

            var tokens: Array<Token> = arrayOf()
            var buffer = ""
            var isSymbols = false
            var insideString = false

            // commits buffer to `tokens`
            fun commit() {
                if (buffer.isEmpty()) return
                val type = TokenType.getFrom(buffer)
                tokens += Token(type, buffer)
                buffer = ""
            }

            run chars@{
                line.forEach char@{ char: Char ->
                    if (char == ' ' && !insideString) {
                        return@char commit()
                    }
                    if (char == '"') {
                        insideString = !insideString
                    }

                    if (buffer.isEmpty()) {
                        buffer += char
                        isSymbols = symbols.contains(char)
                        return@char
                    }

                    if (symbols.contains(char) == isSymbols) {
                        buffer += char
                    } else {
                        return@char commit().also { buffer = char.toString() }
                    }

                    if (arrayOf(*operators, *conditions, *assignments, *comments).contains(buffer)) {
                        if (TokenType.getFrom(buffer) == TokenType.COMMENT) return@chars commit()
                        return@char commit()
                    }
                }
            }
            commit()

            if (debug) println("Executing line '$line'")
            if (debug) tokens.forEach { println("\t${it.type} ${it.value}") }
            try {
                interpretTokens(tokens)
            } catch (e: InternalInterpreterException) {
                throw InterpreterException(e.message, lineIndex)
            }
            lineIndex++
        }
    }

    private fun interpretTokens(tokens: Array<Token>) {
        var tokenIndex = 0
        for (token in tokens) {
            if (token.type == TokenType.COMMENT) return
        }

//        if (tokens.isOperator()) {
//            val data1 = tokens[0].toValue(data)
//            val value1 = data1.first.cast<Float>(tokens[0].line)
//            val operator = tokens[1]
//            val data2 = tokens[2].toValue(data)
//            val value2 = data2.first.cast<Float>(tokens[0].line)
//
//            if (debug) println("$data1 $operator $data2 = ")
//            when (operator.value) {
//                "+" -> println(value1 + value2)
//                "-" -> println(value1 - value2)
//                "*" -> println(value1 * value2)
//                "/" -> println(value1 / value2)
//                "^" -> println(value1.pow(value2))
//                "%" -> println(value1 % value2)
//            }
//        } else if (tokens.isAssignemnt()) {
//            val name = tokens[0].value
//            val (value, type) = tokens[2].toValue(data)
//            val constructed = Data(name, value, type)
//
//            data.find { it.name == tokens[0].value }?.let { variable ->
//                data[data.indexOf(variable)] = constructed
//            } ?: run {
//                data += Data(name, value, type)
//            }
//        }
    }

    private fun evaluateTokens(tokens: Array<Token>) {
//        if (tokens.isOperator()) {
//            val data1 = tokens[0].toValue(data)
//            val value1 = data1.first.cast<Float>(tokens[0].line)
//            val operator = tokens[1]
//            val data2 = tokens[2].toValue(data)
//            val value2 = data2.first.cast<Float>(tokens[0].line)
//
//            if (debug) println("$data1 $operator $data2 = ")
//            when (operator.value) {
//                "+" -> println(value1 + value2)
//                "-" -> println(value1 - value2)
//                "*" -> println(value1 * value2)
//                "/" -> println(value1 / value2)
//                "^" -> println(value1.pow(value2))
//                "%" -> println(value1 % value2)
//            }
//        }
    }
}
