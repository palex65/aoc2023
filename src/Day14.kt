/* --- Advent of Code 2023 - Day 14: Parabolic Reflector Dish --- */

package day14

import println
import readInput
import kotlin.time.measureTime

enum class Item(val sym: Char) { ROCK('O'), CUBE('#') }
typealias Platform = List<Array<Item?>>

fun List<String>.parsePlatform(): Platform =
    map { it.map { c -> Item.entries.firstOrNull { it.sym==c } }.toTypedArray() }

fun Platform.copy(): Platform = map { it.copyOf() }

fun Platform.print(label: String) {
    println("$label: load=${totalLoad()}")
    forEach { row ->
        row.forEach { print(it?.sym ?: '.') }
        println("")
    }
}

fun Platform.tiltNorth() {
    for(l in 0..lastIndex) {
        val line = get(l)
        for(c in 0..line.lastIndex) if (line[c] == Item.ROCK) {
            var i = l-1; while (i >= 0 && this[i][c] == null) i--
            if (i < l-1) { this[i + 1][c] = Item.ROCK; line[c] = null }
        }
    }
}
fun Platform.tiltSouth() {
    for(l in lastIndex downTo 0) {
        val line = get(l)
        for(c in 0..line.lastIndex) if (line[c] == Item.ROCK) {
            var i = l + 1; while (i <= lastIndex && this[i][c] == null) i++
            if (i > l + 1) { this[i -1][c] = Item.ROCK; line[c] = null }
        }
    }
}
fun Platform.tiltWest() {
    for(c in 0..get(0).lastIndex) {
        for(l in 0..lastIndex) if (this[l][c] == Item.ROCK) {
            var i =c-1; while (i >= 0 && this[l][i] == null) i--
            if (i < c-1) { this[l][i+1] = Item.ROCK; this[l][c] = null }
        }
    }
}
fun Platform.tiltEast() {
    for(c in get(0).lastIndex downTo 0) {
        for(l in 0..lastIndex) if (this[l][c] == Item.ROCK) {
            var i =c+1; while (i <= get(0).lastIndex && this[l][i] == null) i++
            if (i > c+1) { this[l][i-1] = Item.ROCK; this[l][c] = null }
        }
    }
}

fun Platform.totalLoad(): Int =
    mapIndexed { idx, row -> row.count { it==Item.ROCK } * (size-idx) }
        .sum()

fun Platform.cycle() {
    tiltNorth(); tiltWest(); tiltSouth(); tiltEast()
}

fun Platform.number() = flatMapIndexed { l, line ->
    line.mapIndexedNotNull { c, it -> if (it==Item.ROCK) l*line.size + c else null }
}.sum()

fun main() {
    fun part1(p: Platform): Int {
        p.tiltNorth()
        return p.totalLoad()
    }

    fun part2(p: Platform): Int {
        val res = mutableListOf<Int>()
        var num = 0
        while(num !in res) {
            res.add(num)
            p.cycle()
            num = p.number()
        }
        val startLoop = res.indexOf(num)
        val loopSize = res.size - startLoop
        println("Loop detected: $num after ${res.size} cicles in cycle $startLoop")
        val lastLoads = mutableListOf(p.totalLoad())
        repeat(loopSize-1) {
            p.cycle()
            check(p.number() == res[startLoop+it+1])
            lastLoads.add(p.totalLoad())
        }
        val idx = (1000000000 - startLoop) % loopSize
        return lastLoads[idx]
    }

    val testInput = readInput("Day14_test").parsePlatform()
    check(part1(testInput.copy()) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14").parsePlatform()
    println( measureTime { part1(input.copy()).println() } ) // 105784 - 2ms
    println( measureTime { part2(input).println() } ) // 91286 - 100ms
}
