/* --- Advent of Code 2023 - Day 6: Wait For It --- */

package day06

import println
import readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt
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

// a*x^2 + b*x + c = 0 =>  x = (-b +- sqrt(b^2 - 4*a*c)) / (2*a)
fun Race.countBests(): Int {
    // distance = speed * (time-speed)
    // d = s * (t-s) =>  s^2 - t*s + d = 0 -> a=1, b=-t, c=d
    // s = (t +- sqrt(t^2 - 4*d)) / 2
    val s = (time-sqrt( (time.toDouble()*time - 4*distance) )) / 2
    val first = floor(s).toInt() + 1
    val count = time-2*first + 1
    return count
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
    println( measureTime { part1(input).println() } ) // 275724 - a)1.5ms - 1ms
    println( measureTime { part2(input).println() } ) // 37286485 - a)120ms - 100us
}
