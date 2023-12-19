/* --- Advent of Code 2023 - Day ??? --- */

package day00

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

fun main() {
    fun part1(input: Input): Int = 0

    fun part2(input: Input): Int = 1

    val testInput = readInput("Day00_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 1)

    val input = readInput("Day00")
    println( measureTime { part1(input).println() } ) // 0 - 1ms
    println( measureTime { part2(input).println() } ) // 1 - 50ms
}
