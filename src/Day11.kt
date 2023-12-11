/* --- Advent of Code 2023 - Day 11: Cosmic Expansion --- */

package day11

import println
import readInput
import kotlin.math.absoluteValue
import kotlin.time.measureTime

data class Point(val row: Int, val col: Int)

typealias Image = List<Point>

fun List<String>.parseImage(): Image = buildList {
    val img = this@parseImage
    img.forEachIndexed{ row, line ->
        line.forEachIndexed { col , p -> if (p=='#') add(Point(row,col))}
    }
}

fun Image.expand(times: Int): Image {
    val img = toMutableList()
    val maxRow = maxOf { it.row }
    for(r in maxRow-1 downTo 0)
        if ( img.none{ it.row==r } )
            img.forEachIndexed { idx, p-> if (p.row>r) img[idx] = Point(p.row+times-1,p.col) }
    val maxCol = maxOf { it.col }
    for(c in maxCol-1 downTo 0)
        if ( img.none{ it.col==c } )
            img.forEachIndexed { idx, p-> if (p.col>c) img[idx] = Point(p.row,p.col+times-1) }
    check(img.all { it.row>=0 && it.col>=0 })
    return img
}

fun Image.sumOfDistances(): Long {
    var total = 0L
    forEachIndexed { i1, g1 ->
        for(i2 in i1+1..lastIndex) {
            val g2 = this[i2]
            total += (g1.row-g2.row).absoluteValue +
                     (g1.col-g2.col).absoluteValue
        }
    }
    return total
}

fun main() {
    fun part1(input: Image, expandTimes: Int=2): Long {
        val img = input.expand(expandTimes)
        return img.sumOfDistances()
    }

    val testInput = readInput("Day11_test").parseImage()
    check(part1(testInput) == 374L)
    check(part1(testInput,10) == 1030L)
    check(part1(testInput,100) == 8410L)

    val input = readInput("Day11").parseImage()
    println( measureTime { part1(input).println() } ) // 10231178 - 10ms
    println( measureTime { part1(input,1000000).println() } ) // 622120986954 - 10ms
}
