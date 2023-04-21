package interpreter

data class Data(val name: String, val value: Any, val type: DataType)

inline fun <reified T> Any.cast(line: Int): T {
    if (this !is T) throw InterpreterTypeException("Illegal type", line)
    return this
}

enum class DataType {
    STRING,
    NUMBER,
    BOOLEAN
}
