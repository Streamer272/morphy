package interpreter

data class Data(val name: String, var value: Value, val constant: Boolean)
typealias DataArray = Array<Data>

enum class DataType {
    STRING,
    NUMBER,
    BOOLEAN
}
