/* --- Advent of Code 2023 - Day 18: Lavaduct Lagoon --- */

package day18

import println
import readInput
import kotlin.math.absoluteValue
import kotlin.time.measureTime

typealias Input = List<String>

enum class Direction(val letter: Char, val dRow: Int, val dCol: Int) {
    RIGHT('R',0,+1), DOWN('D',+1,0), LEFT('L',0,-1), UP('U',-1,0),
}

data class DigAction(val dir: Direction, val length: Int)

typealias DigPlan = List<DigAction>

fun Input.parsePlan(): DigPlan =
    map { line ->
        val (dir,len) = line.split(' ')
        DigAction(Direction.entries.first { it.letter == dir[0] }, len.toInt())
    }

fun Input.parsePlan2(): DigPlan =
    map { line ->
        val code = line.split(' ')[2].substring(2..7)
        val dir = Direction.entries.first { it.ordinal == code.last().digitToInt() }
        DigAction(dir, code.dropLast(1).toInt(16))
    }

data class Point(val row: Int, val col: Int)

fun Point.move(dir: Direction, length: Int=1) = Point(row+dir.dRow*length, col+dir.dCol*length)

fun main() {
    // Area of polygon  and  Pick's theorem
    // A = | (x1*y2 - x2*y1) + (x2*y3 - x3*y2) + ... + (xn*y1 - x1*yn) | / 2
    // I = A - B/2 + 1 where B is boundary points and I is internal points
    // Total = I + B
    fun computeCubes(plan: DigPlan): Long {
        var vertex = Point(0,0)
        var area = 0L
        var perimeter = 0L
        plan.forEach { da: DigAction ->
            val next = vertex.move(da.dir, da.length)
            area += vertex.row.toLong() * next.col - vertex.col.toLong() * next.row
            perimeter += da.length
            vertex = next
        }
        area = area.absoluteValue / 2
        val internals = area - perimeter/2 +1
        return internals + perimeter
    }

    fun part1(input: Input): Long  = computeCubes(input.parsePlan())

    fun part2(input: Input): Long = computeCubes(input.parsePlan2())

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)
//
    val input = readInput("Day18")
    println( measureTime { part1(input).println() } ) // 62573 - 3ms
    println( measureTime { part2(input).println() } ) // 54662804037719 - 2ms
}
