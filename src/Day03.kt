/* --- Advent of Code 2023 - Day 3: Gears Ratios --- */

package day03

import println
import readInput

/* -- Classes for representing the engine schematic --*/
data class Point(val x: Int, val y: Int)            // Position in engine schematic
data class Symbol(val id: Char, val pos: Point)     // Symbol in engine schematic
data class Number(val id: String, val pos: Point)   // Number in engine schematic
data class Engine(val symbols: List<Symbol>, val numbers: List<Number>)

/**
 * Parses a sequence of digits [from] index in a String.
 */
fun String.parseDigits(from: Int): String {
    var end = from
    while (end < length && this[end].isDigit()) end++
    return substring(from,end)
}

/**
 * Parses the engine schematic from the given list of strings (puzzle map).
 */
fun List<String>.parseEngine(): Engine {
    val symbols = mutableListOf<Symbol>()
    val numbers = mutableListOf<Number>()
    forEachIndexed { y, line ->
        var x = 0
        while (x < line.length) {
            val c = line[x]
            when {
                c.isDigit() -> line.parseDigits(from = x).let {
                    numbers.add(Number(it, Point(x,y)))
                    x += it.length
                }
                c!='.' -> symbols.add(Symbol(c, Point(x++,y)))
                else -> x++
            }
        }
    }
    return Engine(symbols, numbers)
}

/**
 * Returns the list of points adjacent to this point.
 */
val Point.adjacent: List<Point> get() = listOf(
    Point(x-1,y-1), Point(x,y-1), Point(x+1,y-1),
    Point(x-1,y),                 Point(x+1,y),
    Point(x-1,y+1), Point(x,y+1), Point(x+1,y+1)
)

/**
 * Verify if this number is adjacent to the given symbol.
 */
fun Number.isAdjacentTo(sym: Symbol) =
    sym.pos.adjacent.any { it.y == pos.y && it.x in pos.x..<pos.x+id.length }

fun main() {
    fun part1(engine: Engine): Int = engine.numbers
        .filter { num -> engine.symbols.any { sym -> num.isAdjacentTo(sym) } }
        .sumOf { it.id.toInt() }

    fun part2(engine: Engine) = engine.symbols
        .filter { it.id == '*' }
        .map { star -> engine.numbers.filter { it.isAdjacentTo(star) } }
        .filter { it.size == 2 }
        .sumOf { (n1, n2) -> n1.id.toInt() * n2.id.toInt() }

    val testEngine = readInput("Day03_test").parseEngine()
    check(part1(testEngine) == 4361)
    check(part2(testEngine) == 467835)

    val engine = readInput("Day03").parseEngine()
    part1(engine).println() // 550064
    part2(engine).println() // 85010461
}
