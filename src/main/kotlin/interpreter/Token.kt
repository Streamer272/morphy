package interpreter

val stringRegex = Regex("(?<=^\").*(?=\"$)")
val numberRegex = Regex("^\\d+$")
val floatRegex = Regex("^[\\d.]+$")
val boolRegex = Regex("^true|false$")

data class Token(val type: TokenType, var value: String)

typealias Value = Pair<Any, DataType>

fun String.toValue(data: DataArray): Value {
    println("parsing '$this'")
    stringRegex.find(this)?.let { match ->
        return Value(match.value, DataType.STRING)
    }
    numberRegex.find(this)?.let { match ->
        return Value(match.value.toFloat(), DataType.NUMBER)
    }
    floatRegex.find(this)?.let { match ->
        return Value(match.value.toFloat(), DataType.NUMBER)
    }
    boolRegex.find(this)?.let { match ->
        return Value(match.value == "true", DataType.BOOLEAN)
    }

    data.find { it.name == this }?.let { variable ->
        return Value(variable.value, variable.type)
    }

    throw InternalTypeException("Illegal type")
}

inline fun <reified T> Value.to(): Pair<T, DataType> {
    if (this.first !is T) throw InternalTypeException("Illegal type")
    return Pair(this.first as T, this.second)
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
    EVALUATION,
    COMMENT,
    KEYWORD;

    companion object {
        fun getFrom(value: String): TokenType = when {
            operators.contains(value) -> OPERATOR
            conditions.contains(value) -> CONDITION
            assignments.contains(value) -> ASSIGNMENT
            comments.contains(value) -> COMMENT
            keywords.contains(value) -> KEYWORD
            else -> EVALUATION
        }
    }
}
