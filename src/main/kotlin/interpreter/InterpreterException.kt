package interpreter

open class InterpreterException(message: String, val line: Int) : Exception(message)
class InterpreterTypeException(message: String, line: Int) : InterpreterException("TypeError: $message", line)
