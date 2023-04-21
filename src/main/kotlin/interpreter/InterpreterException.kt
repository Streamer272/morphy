package interpreter

open class InternalInterpreterException(override val message: String) : Exception(message)
open class InternalTypeException(message: String) : InternalInterpreterException("TypeError: $message")

open class InterpreterException(message: String, val line: Int) : InternalInterpreterException(message)
class InterpreterTypeException(message: String, val line: Int) : InternalTypeException(message)
