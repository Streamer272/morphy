package interpreter

val stringRegex = Regex("(?<=^\").*(?=\"$)")
val numberRegex = Regex("^\\d+$")
val floatRegex = Regex("^[\\d.]+$")
val boolRegex = Regex("^true|false$")

data class Token(val type: TokenType, var value: String)

fun Token.toValue(data: Array<Data>, line: Int): Pair<Any, DataType> {
    stringRegex.find(this.value)?.let { match ->
        return Pair(match.value, DataType.STRING)
    }
    numberRegex.find(this.value)?.let { match ->
        return Pair(match.value.toFloat(), DataType.NUMBER)
    }
    floatRegex.find(this.value)?.let { match ->
        return Pair(match.value.toFloat(), DataType.NUMBER)
    }
    boolRegex.find(this.value)?.let { match ->
        return Pair(match.value == "true", DataType.BOOLEAN)
    }

    data.find { it.name == this.value }?.let { variable ->
        return Pair(variable.value, variable.type)
    }

    throw InterpreterTypeException("Illegal type", line)
}

fun Array<Token>.isOperator(): Boolean {
    return this.size == 3 &&
            this[0].type == TokenType.EVALUATION &&
            this[1].type == TokenType.OPERATOR &&
            this[2].type == TokenType.EVALUATION
}

fun Array<Token>.isCondition(): Boolean {
    return this.size == 3 &&
            this[0].type == TokenType.EVALUATION &&
            this[1].type == TokenType.CONDITION &&
            this[2].type == TokenType.EVALUATION
}

fun Array<Token>.isAssignemnt(): Boolean {
    return this.size == 3 &&
            this[0].type == TokenType.EVALUATION &&
            this[1].type == TokenType.ASSIGNMENT &&
            this[2].type == TokenType.EVALUATION
}

enum class TokenType {
    OPERATOR,
    CONDITION,
    ASSIGNMENT,
    EVALUATION;

    companion object {
        fun getBased(on: Char): TokenType = when {
            operators.contains(on) -> OPERATOR
            conditions.contains(on) -> CONDITION
            assignments.contains(on) -> ASSIGNMENT
            else -> EVALUATION
        }
    }
}
