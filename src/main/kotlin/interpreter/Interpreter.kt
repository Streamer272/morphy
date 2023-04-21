package interpreter

import java.io.File
import kotlin.math.pow

val operators = arrayOf('+', '-', '*', '/', '^', '%')
val conditions = arrayOf('&', '|', '!', '?')
val assignments = arrayOf('=')

class Interpreter(private val file: File, private val debug: Boolean) {
    var data: Array<Data> = arrayOf()

    fun interpret() {
        val lines = this.file.readText().split(Regex("[\n;]")).map { it.trim() }

        var lineIndex = 1
        lines.forEach lines@{ line: String ->
            if (line.isEmpty()) return@lines

            var tokens: Array<Token> = arrayOf()
            line.forEach chars@{ char: Char ->
                if (char == ' ') return@chars

                val type = TokenType.getBased(char)
                if (tokens.isNotEmpty() && tokens.last().type == type) {
                    tokens[tokens.lastIndex].value += char.toString()
                } else {
                    tokens += Token(type, char.toString())
                }
            }

            if (debug) println("Executing line '$line'")
            interpretTokens(tokens, lineIndex)
            lineIndex++
        }
    }

    fun interpretTokens(tokens: Array<Token>, line: Int) {
        if (tokens.isOperator()) {
            val data1 = tokens[0].toValue(data, line)
            val value1 = data1.first.cast<Float>(line)
            val operator = tokens[1]
            val data2 = tokens[2].toValue(data, line)
            val value2 = data2.first.cast<Float>(line)

            if (debug) println("$data1 $operator $data2 = ")
            when (operator.value) {
                "+" -> println(value1 + value2)
                "-" -> println(value1 - value2)
                "*" -> println(value1 * value2)
                "/" -> println(value1 / value2)
                "^" -> println(value1.pow(value2))
                "%" -> println(value1 % value2)
            }
        } else if (tokens.isAssignemnt()) {
            val name = tokens[0].value
            val (value, type) = tokens[2].toValue(data, line)
            val constructed = Data(name, value, type)

            data.find { it.name == tokens[0].value }?.let { variable ->
                data[data.indexOf(variable)] = constructed
            } ?: run {
                data += Data(name, value, type)
            }
        }
    }
}
