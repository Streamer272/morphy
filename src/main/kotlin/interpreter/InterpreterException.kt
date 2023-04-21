package interpreter

open class InternalInterpreterException(override val message: String) : Exception(message)
open class InternalTypeException(message: String) : InternalInterpreterException("TypeError: $message")
open class InternalSyntaxException(message: String) : InternalInterpreterException("SyntaError: $message")
open class InternalConstantException(message: String) : InternalInterpreterException("ConstantError: $message")

open class InterpreterException(message: String, val line: Int) : InternalInterpreterException(message)
