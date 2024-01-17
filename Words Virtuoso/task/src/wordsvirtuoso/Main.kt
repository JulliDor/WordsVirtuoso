package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess
class Words(private val wordsFile:File, private val candidatesFile:File) {
    init {
        when {
            !wordsFile.exists() -> println("Error: The words file ${wordsFile.name} doesn't exist.").also { exitProcess(0) }
            !candidatesFile.exists() -> println("Error: The candidate words file ${candidatesFile.name} doesn't exist.").also { exitProcess(0) }
            else ->  {
                checkListWords(wordsFile)
                checkListWords(candidatesFile)
                checkEntryWords()
                println("Words Virtuoso")
            }
        }
    }
   private fun checkListWords(file: File) {
        file.readLines().count { s -> !s.matches(Regex("""[a-zA-Z]{5}""")) || s.toSet().size != 5 }.also { if (it != 0) { println("Error: $it invalid words were found in the ${file.name} file."); exitProcess(0) } }
    }
    private fun checkEntryWords() {
        candidatesFile.readLines().map { it.lowercase() }.count {!wordsFile.readLines().map { s -> s.lowercase() }.contains(it) }.also { if (it != 0) {
                    println("Error: $it candidate words are not included in the ${wordsFile.name} file."); exitProcess(0) }
            }
    }
    private var n = 0
    private fun checkInput(file: File): String {
        while (true) {
            val word = println("\nInput a 5-letter word:").run { readln() }.also {n++}
            when {
                word == "exit" -> { println("The game is over."); exitProcess(0) }
                word.length != 5 -> println("The input isn't a 5-letter word.")
                !word.matches(Regex("""[a-zA-Z]+""")) -> println("One or more letters of the input aren't valid.")
                word.toSet().size != 5 -> println("The input has duplicate letters.")
                !file.readLines().contains(word) -> println("The input word isn't included in my words list.")
                else -> return word
            }
        }
    }
    fun game() {
        val candidateWords = wordsFile.readLines()
        val secretWord = candidateWords.elementAt(Random.nextInt(candidateWords.size))
        //println(secretWord)
        val wrongCharList = mutableListOf<Char>()
        val tryList = mutableListOf<String>()
        val start = System.currentTimeMillis()
        while (true) {
            val word = checkInput(wordsFile).lowercase()
            var output = ""
            for ((i, v) in word.withIndex()) {
                output += if (secretWord.contains(v) && secretWord.indexOf(v) == i) "\u001b[48:5:10m${v.uppercase()}\u001B[0m"
                else if (secretWord.contains(v)) "\u001b[48:5:11m${v.uppercase()}\u001B[0m"
                else "\u001b[48:5:7m${v.uppercase()}\u001B[0m".also {wrongCharList.add(v)}
            }
            tryList.add(output)
            if (secretWord == word){
                val duration = System.currentTimeMillis() - start
                println("\n" + tryList.joinToString("\n") + "\nCorrect!\n" + if (n == 1) "Amazing luck! The solution was found at once." else "The solution was found after $n tries in ${duration / 1000} seconds.")
                return
            }
            else {
                println("\n" + tryList.joinToString("\n"))
                println("\n" + "\u001b[48:5:14m${wrongCharList.toSet().sorted().joinToString("").uppercase()}\u001B[0m" )
            }
        }
    }
}

fun main(args:Array<String>) {
    if (args.size != 2) { println("Error: Wrong number of arguments."); return }
    Words(File(args[0]), File(args[1])).game()
}
