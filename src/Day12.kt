/* --- Advent of Code 2023 - Day 12: Hot Springs --- */

package day12

import println
import readInput
import kotlin.time.measureTime

data class SpringRow(val pattern: String, val groups: List<Int>)

enum class State(val char: Char) { OPERATIONAL('.'), DAMAGE('#') }
data class Spring(val state: State, val fixed: Boolean)

fun Char.toSpring() =
    if (this=='?') Spring(State.OPERATIONAL, fixed = false)
    else Spring(State.entries.first { this==it.char }, fixed = true)

typealias Pattern = List<Spring>

class Springs(val line: List<Spring>, val groups: List<Int>)

fun Pattern.countArrange(groups: List<Int>): Long {
    val dp = List(size) {
        List(groups.size + 1) { g ->
            LongArray( (if (g<groups.size) groups[g] else 0) + 1) { -1 }
        }
    }
    fun cnt(i: Int, g: Int, u: Int): Long {
        if (i >= size) return if (g >= groups.size || g == groups.lastIndex && u == groups[g]) 1 else 0
        val n = dp[i][g][u]; if (n >= 0) return n
        val s = this[i]
        var res = 0L
        if (! s.fixed || s.state==State.OPERATIONAL) {
            if (u > 0 && groups[g] == u) res += cnt(i+1, g+1, 0)
            else if (u == 0) res += cnt(i+1, g, 0)
        }
        if (! s.fixed || s.state==State.DAMAGE) {
            if (g < groups.size && u < groups[g]) res += cnt(i+1, g, u+1)
        }
        dp[i][g][u] = res
        return res
    }
    return cnt(0, 0, 0)
}

fun List<String>.parseRows(): List<SpringRow> =
    map { line ->
        val (pattern, groups) = line.split(' ')
        SpringRow(pattern, groups.split(',').map { it.toInt() })
    }

fun main() {
    fun part1(input: List<SpringRow>): Long = input
        .map { Springs(it.pattern.map { it.toSpring() }, it.groups) }
        .sumOf { it.line.countArrange(it.groups) }

    fun part2(input: List<SpringRow>): Long = input
        .map { row ->
            Springs(
                ("${row.pattern}?".repeat(4) + row.pattern).map { it.toSpring() },
                List(5) { row.groups }.flatten()
            )
        }
        .sumOf { it.line.countArrange(it.groups) }

    val testInput = readInput("Day12_test").parseRows()
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12").parseRows()
    println( measureTime { part1(input).println() } ) // 8022 - 20ms
    println( measureTime { part2(input).println() } ) // 4968620679637 - 100ms
}
