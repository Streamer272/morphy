package interpreter

import java.io.File
import kotlin.math.pow

val operators = arrayOf("+", "-", "*", "/", "^", "%")
val conditions = arrayOf("&&", "||", "!", "??")
val assignments = arrayOf("=")
val comments = arrayOf("//")
val declarationConstant = "val"
val declarations = arrayOf("var", declarationConstant)
val keywordType = "type"
val keywords = arrayOf(keywordType, *declarations)

val symbols = arrayOf('+', '-', '*', '/', '^', '%', '&', '|', '!', '?', '=')

class Interpreter(private val file: File, private val debug: Boolean) {
    private var data: DataArray = arrayOf()

    fun parse() {
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

                    if (symbols.contains(char) == isSymbols || insideString) {
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

            if (debug) println("Interpreting '$line'")
            if (debug) tokens.forEach { println("\t${it.type} ${it.value}") }
            try {
                interpret(tokens)
            } catch (e: InternalInterpreterException) {
                throw InterpreterException(e.message, lineIndex)
            }
            lineIndex++
        }
    }

    private fun interpret(tokens: Array<Token>) {
        var declaration: Declaration? = null
        var operand: Value? = null
        var operandName: String? = null
        var operator: String? = null
        var type = false

        for (token in tokens) {
            if (declaration == null) {
                when (token.type) {
                    TokenType.COMMENT -> return
                    TokenType.KEYWORD -> {
                        if (declarations.contains(token.value)) {
                            declaration = Declaration(token.value == declarationConstant, "", false)
                        }
                        if (keywordType == token.value) {
                            type = true
                        }
                    }

                    TokenType.EVALUATION -> {
                        if (type) {
                            println(token.value.toValue(data).second.name.lowercase())
                            type = false
                        } else if (operand != null) {
                            if (operator == null) throw InternalSyntaxException("perator expected")

                            val value = token.value.toValue(data)
                            when (operator) {
                                "+" -> println(operand.toFloat() + value.toFloat())
                                "-" -> println(operand.toFloat() - value.toFloat())
                                "*" -> println(operand.toFloat() * value.toFloat())
                                "/" -> println(operand.toFloat() / value.toFloat())
                                "^" -> println(operand.toFloat().pow(value.toFloat()))
                                "%" -> println(operand.toFloat() % value.toFloat())
                            }
                            operand = null
                            operator = null
                        } else {
                            operand = token.value.toValue(data)
                            operandName = token.value
                        }
                    }

                    TokenType.OPERATOR -> {
                        operator = token.value
                    }

                    TokenType.ASSIGNMENT -> {
                        if (operandName != null) {
                            declaration = Declaration(false, operandName, true)
                        }
                    }

                    else -> {}
                }
            } else {
                when (token.type) {
                    TokenType.EVALUATION -> {
                        if (!declaration.nextValue) {
                            declaration.name = token.value
                        } else {
                            val value = token.value.toValue(data)
                            val existing = data.find { it.name == declaration!!.name }
                            if (existing != null) {
                                if (existing.constant) {
                                    throw InternalConstantException("${existing.name} is a constant")
                                } else if (existing.value.second != value.second) {
                                    throw InternalTypeException("${existing.name} is a ${existing.value.second.name.lowercase()}, but ${value.second.name.lowercase()} was provided")
                                }

                                existing.value = value
                            } else {
                                data += Data(declaration.name, value, declaration.constant)
                            }
                            declaration = null
                        }
                    }

                    TokenType.ASSIGNMENT -> {
                        declaration.nextValue = true
                    }

                    else -> {}
                }
            }
        }

        if (operand != null) {
            if (operand.second == DataType.NUMBER) {
                var k = 1
                if (operator != null && operator == "-") k = -1
                println(operand.toFloat() * k)
            } else {
                println(operand.first)
            }
        }
    }
}
