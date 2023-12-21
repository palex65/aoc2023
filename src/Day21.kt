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

fun Garden.show(plots: Set<Point>) {
    val dim = dimension
    for (row in 0..<dim.rows) {
        for (col in 0..<dim.cols) {
            val p = Point(row, col)
            print(if (p in plots) 'O' else this[p].sym)
        }
        println("")
    }
    println("-- Plots: ${plots.size} --")
}

fun Garden.plotsCount(start: Point, steps: Int): Int {
    var plots = setOf(start)
    val dim = dimension
    repeat(steps) {
        val newPlots = mutableSetOf<Point>()
        for (plot in plots) {
            newPlots.addAll( Direction.entries
                .map { plot.move(it) }
                .filter { it.isInside(dim) && this[it.row][it.col]==Tile.PLOT }
            )
        }
        plots = newPlots
        //show(plots)
    }
    //show(plots)
    return plots.size
}

fun main() {
    fun part1(input: Input, steps: Int): Int {
        val (garden, start) = input.parseInfo()
        //garden.dimension.println()
        return garden.plotsCount(start, steps)
    }

    fun part2(input: Input, steps: Int): Long {
        val (garden, start) = input.parseInfo()
        val (height, width) = garden.dimension
        val hist = mutableListOf<Set<Point>>()
        var plots = setOf(start)
        repeat(steps) {
            val newPlots = mutableSetOf<Point>()
            for (plot in plots) {
                newPlots.addAll( Direction.entries
                    .map { plot.move(it) }
                    .filter { garden[it]==Tile.PLOT }
                )
            }
            hist.add(plots)
            plots = newPlots
           //println(" ${it + 1}\t\t${plots.size}")
        }
//        val minRow = minOf(plots.minOf { it.row },0)
//        val minCol = minOf(plots.minOf { it.col },0)
//        val maxRow = maxOf(plots.maxOf { it.row },height)
//        val maxCol = maxOf(plots.maxOf { it.col },width)
//        for (row in minRow .. maxRow) {
//            for (col in minCol..maxCol) {
//                val p = Point(row, col)
//                //if (it>0 && p in hist[it-1]) print('*')
//                //else
//                    print(if (p in plots) 'O' else garden[p].sym)
//            }
//            println("")
//        }
        return plots.size.toLong()
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput,6) == 16)
    check(part2(testInput,50) == 1594L)
    check(part2(testInput,500) == 167004L)
//    check(part2(testInput,5000) == 16733044L)
//
    val input = readInput("Day21")
    println( measureTime { part1(input,64).println() } ) // 3751 - 50ms
//    println( measureTime { part2(input,26501365).println() } ) // 1 - 50ms
}
