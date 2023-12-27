/* --- Advent of Code 2023 - Day 21: Step Counter --- */

package day21

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

data class Point(val row: Int, val col: Int)
data class Dimension(val rows: Int, val cols: Int)
enum class Direction(val dRwo: Int, val dCol: Int) { UP(-1,0), DOWN(1,0), LEFT(0,-1), RIGHT(0,1) }

fun Point.move(dir: Direction) = Point(row+dir.dRwo, col+dir.dCol)
fun Point.isInside(dim: Dimension) = row in 0 ..< dim.rows && col in 0 ..< dim.cols

enum class Tile(val sym: Char) { PLOT('.'), ROCK('#') }
typealias Garden = List<List<Tile>>

val Garden.dimension get() = Dimension(size,get(0).size)

operator fun Garden.get(p: Point): Tile {
    val (height, width) = dimension
    return get(p.row.mod(height))[p.col.mod(width)]
}

data class Info(val garden: Garden, val start: Point)

fun Input.parseInfo(): Info {
    var start = Point(-1,-1)
    val garden = mapIndexed { row, line ->
        line.mapIndexed { col, tile ->
            Tile.entries.firstOrNull { it.sym==tile } ?: Tile.PLOT.also { start = Point(row,col) }
        }
    }
    return Info(garden, start)
}

fun Garden.plotCounters(start: Point, steps: Int): List<Int> {
    val dim = dimension
    val plotsByStep = mutableListOf(1)
    var plots = setOf(start)
    val totalPlots = plots.toMutableSet()
    for(step in 1 .. steps) {
        plots = plots.flatMap { plot -> Direction.entries.mapNotNull {
            plot.move(it).takeIf { it.isInside(dim) && this[it.row][it.col]==Tile.PLOT && it !in totalPlots}
        } }.toSet()
        totalPlots += plots
        plotsByStep.add(plots.size)
    }
    return plotsByStep
}

inline fun List<Int>.sumIfIdx(cond: (idx: Int) -> Boolean) = filterIndexed{ idx, _ -> cond(idx) }.sum()

fun main() {
    fun part1(input: Input, steps: Int): Int {
        val (garden, start) = input.parseInfo()
        //garden.dimension.println()
        return garden.plotCounters(start, steps).sumIfIdx{ it%2==0 }
    }

    fun part2(input: Input, steps: Int): Long {
        val (garden, start) = input.parseInfo()
        val (height, width) = garden.dimension
        check(height==width)
        val remainder = steps % width
        val plots = garden.plotCounters(start, width)
        val evenFull = plots.sumIfIdx { it%2==0 }
        val oddFull = plots.sumIfIdx { it%2==1 }
        val oddCorner = plots.sumIfIdx { it%2==1 && it>remainder }
        val evenCorner = plots.sumIfIdx { it%2==0 && it>remainder }
        val n = ((steps - width/2) / width).toLong()

        val total = (n*n) * evenFull + (n+1)*(n+1) * oddFull + n * evenCorner - (n+1) * oddCorner
        //println("remainder:$remainder evenFull:$evenFull oddFull:$oddFull evenCorner:$evenCorner oddCorner:$oddCorner")
        //println("n:$n total:$total")
        return total
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput,6) == 16)
//    check(part2(testInput,6) == 16L)
//    println(part2(testInput,50)) // = 1594L
//    check(part2(testInput,500) == 167004L)
//    check(part2(testInput,5000) == 16733044L)
//
    val input = readInput("Day21")
    println( measureTime { part1(input,64).println() } ) // 3751 - 17ms
    println( measureTime { part2(input,26501365).println() } ) // 619407349431167 - 19ms
}
