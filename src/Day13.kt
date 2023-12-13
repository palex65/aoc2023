/* --- Advent of Code 2023 - Day 13: Point of Incidence --- */

package day13

import println
import readInput
import splitBy
import kotlin.time.measureTime

fun String.reflectsIn(n: Int): Boolean {
    var i=n
    var j=n+1
    while(i>=0 && j<length)
        if (get(i--)!=get(j++)) return false
    return true
}

typealias Pattern = List<String>

fun Pattern.print() {
    forEach { println(it) }
}

fun Pattern.rotateLeft(): Pattern =
    (first().lastIndex downTo 0).map { i ->
        (0..lastIndex).map { j ->
            this[j][i]
        }.joinToString("")
    }

fun Pattern.flipColumn(except: Int = -1): Int? {
    val startCols = (0..<first().lastIndex).toList() - (except-1)
    val ns = fold(startCols) { acc, s ->
        acc.filter { n -> s.reflectsIn(n) }
    }
    check(ns.size in 0..1)
    return ns.firstOrNull()?.plus(1)
}

fun Pattern.flipValue(): Int = flipColumn() ?: (checkNotNull(rotateLeft().flipColumn()) * 100)

val Pattern.side get() = size * first().length

fun Pattern.swapSymbol(n: Int): Pattern {
    val w = first().length
    val l = n / w
    return this.mapIndexed { i, s ->
        when (i) {
            l -> {
                val col = n % w
                val sb = StringBuilder(s)
                sb[col] = if (s[col]=='#') '.' else '#'
                sb.toString()
            }
            else -> s
        }
    }
}

fun Pattern.getFirstFlipColumn(except: Int): Int? {
    repeat(side) { n ->
        val fc = swapSymbol(n).flipColumn(except)
        if (fc != null) return fc
    }
    return null
}

fun main() {
    fun part1(patterns: List<Pattern>) = patterns.sumOf { it.flipValue() }

    fun part2(patterns: List<Pattern>): Int =
        patterns.sumOf{ pat ->
            val fv = pat.flipValue()
            pat.getFirstFlipColumn(fv) ?:
            (checkNotNull(pat.rotateLeft().getFirstFlipColumn(fv / 100)) * 100)
        }

    val testInput = readInput("Day13_test").splitBy { it.isEmpty() }
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13").splitBy { it.isEmpty() }
    println( measureTime { part1(input).println() } ) // 29165 - 10ms
    println( measureTime { part2(input).println() } ) // 32192 - 80ms
}
