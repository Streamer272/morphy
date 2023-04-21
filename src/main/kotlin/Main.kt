import interpreter.Interpreter
import interpreter.InterpreterException
import kotlinx.cli.*
import java.io.File
import kotlin.system.exitProcess

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("morphy")
    val debug by parser.option(ArgType.Boolean, "debug", "d", "Enable debug logs").default(false)

    class Run : Subcommand("run", "Run a morphy file") {
        val file by argument(ArgType.String, "file", "File to run").vararg()

        override fun execute() {
            file.forEach {
                val file = File(it)
                if (!file.exists()) {
                    System.err.println("File not found")
                    exitProcess(1)
                }

                try {
                    if (debug) println("Intepreting file ${file.name}")
                    val interpreter = Interpreter(file, debug)
                    interpreter.parse()
                } catch (e: InterpreterException) {
                    System.err.println("'${e.message}' occurred on line ${e.line}")
                }
            }
        }
    }

    val run = Run()
    parser.subcommands(run)
    parser.parse(args)
}
