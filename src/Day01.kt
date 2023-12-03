fun main() {
    fun part1(input: List<String>): Int =
        input.sumOf { line ->
            (line.first { it.isDigit() }.toString() + line.last { it.isDigit() }).toInt()
        }

    val names = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun String.getDigit(range: IntProgression): Int {
        for (idx in range) {
            val c = this[idx]
            if (c.isDigit()) return c-'0'
            names.forEach { name ->
                if (startsWith(name, idx)) return names.indexOf(name) + 1
            }
        }
        error("No digit found")
    }
    fun String.getFirstDigit(): Int = getDigit(indices)
    fun String.getLastDigit(): Int = getDigit(lastIndex downTo 0)

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
