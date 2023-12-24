/* --- Advent of Code 2023 - Day 24: Never Tell Me The Odds --- */

package day24

import println
import readInput
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.measureTime

typealias Input = List<String>

data class Line(val x: Long, val y: Long, val z: Long, val dx: Int, val dy: Int, val dz: Int)

fun Input.parseLines() = map { line ->
    val (pos,vel) = line.split(" @ ").map { it.split(", ") }
    val (x,y,z) = pos.map { it.trim().toLong() }
    val (dx,dy,dz) = vel.map { it.trim().toInt() }
    Line(x,y,z,dx,dy,dz)
}

fun main() {
    fun part1(input: Input, range: LongRange): Int {
        val lines = input.parseLines()
        var count = 0
        for (i in 0..lines.size-2) {
            val l1 = lines[i]
            for (j in i+1 ..< lines.size) {
                val l2 = lines[j]
                val den = l2.dx * l1.dy - l1.dx * l2.dy
                if (den==0) continue  // parallel lines
                val s = ( l1.dx * (l2.y - l1.y) - l1.dy * (l2.x - l1.x) ) / den.toFloat()
                val x = l2.x + l2.dx * s
                val y = l2.y + l2.dy * s
                if (
                    (l1.dx>0 && x < l1.x || l1.dx<0 && x > l1.x || l1.dy>0 && y < l1.y || l1.dy<0 && y > l1.y) // Past for l1
                ||  (l2.dx>0 && x < l2.x || l2.dx<0 && x > l2.x || l2.dy>0 && y < l2.y || l2.dy<0 && y > l2.y) // Past for l2
                ||  (x.roundToLong() !in range || y.roundToLong() !in range) // Out of range
                   ) continue
                //println("$l1 ++ $l2 -> x=$x, y=$y")
                count++
            }
        }
        return count
    }

    fun part2(input: Input): Long {
        val lines = input.parseLines()
        fun computeAxis(ns: List<Long>, ds: List<Int>): Long {
            val minD = -10
            val maxD = +10
            val maxN = ns.maxOrNull()!! + maxD
            fun computeInterceptTime(i: Int, n: Long, d: Int): Int? {
                val den = d-ds[i]
                if (den==0) return null
                val t = (ns[i]-n).toFloat() / den
                val ti = t.toInt()
                return if (t >= 0 && t.roundToInt() == ti) ti else null
            }
            fun intercepts(i: Int, n: Long, d: Int) = computeInterceptTime(i,n,d) != null
            for(n in 0..maxN)
                for(d in minD..maxD) if ( ns.indices.all { intercepts(it,n,d) } ) {
                    val ts = ns.indices.map { computeInterceptTime(it,n,d)!! }
                    if (ts.toSet().size == ts.size && ts.withIndex().all { (i,t) -> n+d*t == ns[i]+ds[i]*t })
                        return n
                }
            return -1L
        }

        val x = computeAxis(lines.map { it.x }, lines.map { it.dx })
        val y = computeAxis(lines.map { it.y }, lines.map { it.dy })
        val z = computeAxis(lines.map { it.z }, lines.map { it.dz })
        return x + y + z
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput, 7L..27) == 2)
//    check(part2(testInput) == 47L)
//
    val input = readInput("Day24")
    println( measureTime { part1(input, 200000000000000..400000000000000).println() } ) // 13910 - 20ms
//    println( measureTime { part2(input).println() } ) // 1 - 50ms
}
