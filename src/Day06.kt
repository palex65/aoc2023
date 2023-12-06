/* --- Advent of Code 2023 - Day 5: If You Give A Seed A Fertilizer --- */

package day06

import println
import readInput
import kotlin.time.measureTime

data class Race(val time: Int, val distance: Long)

fun List<String>.parseRaces(): List<Race> {
    val times = first().substringAfter("Time: ").split(' ').filter { it.isNotBlank() }.map { it.toInt() }
    val distances = last().substringAfter("Distance: ").split(' ').filter { it.isNotBlank() }.map { it.toInt() }
    return times.mapIndexed { idx, time -> Race(time,distances[idx].toLong()) }
}

fun List<String>.parseOneRace(): Race {
    val time = first().substringAfter("Time: ").filter { it.isDigit() }.toInt()
    val distance = last().substringAfter("Distance: ").filter { it.isDigit() }.toLong()
    return Race(time,distance)
}

fun Race.isBestFor(speed: Int) = speed.toLong() * (time-speed) > distance

fun Race.countBests_a() = (1..<time).count { speed -> isBestFor(speed) }

fun Race.countBests(): Int {
    var l=0
    var r=time
    while (l<=r) {
        val m = (l+r)/2
        if (isBestFor(m)) r=m-1 else l=m+1
    }
    return time-2*l + 1
}

fun main() {
    fun part1(input: List<String>): Int = input
        .parseRaces()
        .map { it.countBests() }
        .reduce { acc, i -> acc * i }

    fun part2(input: List<String>): Int = input
        .parseOneRace()
        .countBests()

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    println( measureTime { part1(input).println() } ) // 275724 - a)1ms -  700us
    println( measureTime { part2(input).println() } ) // 37286485 - a)70ms - 60us
}
