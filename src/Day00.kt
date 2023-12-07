/* --- Advent of Code 2023 - Day ??? --- */

package day00

import println
import readInput
import kotlin.time.measureTime

data class D(val a: Int, val b: Int)

fun List<String>.parseD(): List<D> {
    return List(2){ D(0,0) }
}

fun main() {
    fun part1(input: List<String>): Int = 0

    fun part2(input: List<String>): Int = 1

    val testInput = readInput("Day00_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 1)

    val input = readInput("Day00")
    println( measureTime { part1(input).println() } ) // 0 - 1ms
    println( measureTime { part2(input).println() } ) // 1 - 50ms
}
