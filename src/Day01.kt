fun main() {
    fun part1(input: List<String>): Int =
        input.sumOf { line ->
            (line.first { it.isDigit() }.toString() + line.last { it.isDigit() }).toInt()
        }

    val names = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")

    fun String.getFirstDigit(): Int {
        fun find(list: List<String>) =
            list.map { indexOf(it).let { idx-> if(idx>=0) idx else length } to it }.minBy { it.first }
        val (digitIdx,digit) = find(digits)
        val (wordIdx,word) = find(names)
        return if (digitIdx < wordIdx) digit.toInt() else names.indexOf(word) + 1
    }

    fun String.getLastDigit(): Int {
        fun find(list: List<String>) = list.map { lastIndexOf(it) to it }.maxBy { it.first }
        val (digitIdx,digit) = find(digits)
        val (wordIdx,word) = find(names)
        return if (digitIdx > wordIdx) digit.toInt() else names.indexOf(word) + 1
    }

    fun part2(input: List<String>) =
        input.sumOf{ it.getFirstDigit()*10 + it.getLastDigit() }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println() // 55002
    part2(input).println() // 55093
}
