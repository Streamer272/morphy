package interpreter

import java.io.File

val operators = arrayOf("+", "-", "*", "/", "^", "%")
val conditions = arrayOf("&&", "||", "!", "??")
val assignments = arrayOf("=")
val comments = arrayOf("//")
val declarationConstant = "val"
val declarations = arrayOf("var", declarationConstant)
val keywords = arrayOf("if", *declarations)

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
                interpret(tokens)
            } catch (e: InternalInterpreterException) {
                throw InterpreterException(e.message, lineIndex)
            }
            lineIndex++
        }
    }

    private fun interpret(tokens: Array<Token>) {
        var tokenIndex = 0
        var declaration: Declaration? = null
        var operands: Pair<Data, Data>? = null
        var operator: String? = null

        for (token in tokens) {
            when (token.type) {
                TokenType.COMMENT -> return
                TokenType.KEYWORD -> {
                    if (declarations.contains(token.value)) {
                        declaration = Declaration(token.value == declarationConstant, "", false)
                    }
                }

                else -> {}
            }

            if (declaration != null) {
                when (token.type) {
                    TokenType.EVALUATION -> {
                        if (!declaration.nextValue) {
                            declaration.name = token.value
                        } else {
                            val value = token.value.toValue(data)
                            data += Data(declaration.name, value.first, value.second, declaration.constant)
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
    }
}
