/* --- Advent of Code 2023 - Day 24: Never Tell Me The Odds --- */

package day24

import println
import readInput
import kotlin.math.roundToLong
import kotlin.time.measureTime
import day24.Axis.*

typealias Input = List<String>

enum class Axis { X, Y, Z }
val idxAxis = Axis.entries.map { it.ordinal }

data class Line(val pos: List<Long>, val vel: List<Int>)
val Line.dx get() = vel[X.ordinal]
val Line.dy get() = vel[Y.ordinal]
val Line.x get() = pos[X.ordinal]
val Line.y get() = pos[Y.ordinal]

fun Line.at(axis: Axis, t: Float) = pos[axis.ordinal] + vel[axis.ordinal] * t
fun Line.at(axis: Axis, t: Long) = pos[axis.ordinal] + vel[axis.ordinal] * t

fun Line.isFuture(axis: Axis, value: Float) =
    pos[axis.ordinal].let { p -> vel[axis.ordinal].let { v -> v > 0 && p < value || v < 0 && p > value } }

fun Input.parseLines() = map { line ->
    val (pos,vel) = line.split(" @ ").map { it.split(", ") }
    Line( pos.map { it.trim().toLong() }, vel.map { it.trim().toInt() } )
}

inline fun <T> List<T>.countForEachPair(predicate: (T,T) -> Boolean): Int {
    var count = 0
    for (i in 0..size-2) for (j in i+1 ..< size) if (predicate(this[i],this[j])) count++
    return count
}

fun main() {
    fun part1(input: Input, range: LongRange): Int =
        input.parseLines().countForEachPair { l1, l2 ->
            val den = l2.dx * l1.dy - l1.dx * l2.dy
            if (den==0) false  // Parallel lines
            else {
                val s = (l1.dx * (l2.y - l1.y) - l1.dy * (l2.x - l1.x)) / den.toFloat()
                val x = l2.at(X, s)
                val y = l2.at(Y, s)
                l1.isFuture(X, x) && l1.isFuture(Y, y) && l2.isFuture(X, x) && l2.isFuture(Y, y)  // Past for l1 or l2
                    && x.roundToLong() in range && y.roundToLong() in range        // Out of range
            }
        }

    fun part2(input: Input): Long {
        val lines = input.parseLines()
        val times = idxAxis.firstNotNullOf { a ->
            lines.firstNotNullOfOrNull lab@ { l1 -> lines.map { l2 ->
                val tn = l2.pos[a] - l1.pos[a]
                val td = l1.vel[a] - l2.vel[a]
                if (td==0) if (tn==0L) 0L else return@lab null
                else if (tn % td == 0L) tn / td else return@lab null
            } }
        }
        val linesTimes = times.withIndex().filter { it.value > 0 }.take(2).map { lines[it.index] to it.value }
        val (line1,time1) = linesTimes[0]
        val (line2,time2) = linesTimes[1]
        return Axis.entries.sumOf { axis ->
            val p1 = line1.at(axis,time1)
            val p2 = line2.at(axis,time2)
            p1 - (p1 - p2) / (time1 - time2) * time1
        }
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput, 7L..27) == 2)
    check(part2(testInput) == 47L)
//
    val input = readInput("Day24")
    println( measureTime { part1(input, 200000000000000..400000000000000).println() } ) // 13910 - 25ms
    println( measureTime { part2(input).println() } ) // 618534564836937 - 2ms
}
