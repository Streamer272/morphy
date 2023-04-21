package interpreter

data class Data(val name: String, val value: Any, val type: DataType, val constant: Boolean)
typealias DataArray = Array<Data>

inline fun <reified T> Any.cast(line: Int): T {
    if (this !is T) throw InterpreterTypeException("Illegal type", line)
    return this
}

enum class DataType {
    STRING,
    NUMBER,
    BOOLEAN
}
