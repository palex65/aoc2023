/* --- Advent of Code 2023 - Day 9: Mirage Maintenance --- */

package day09

import println
import readInput
import kotlin.time.measureTime

fun List<String>.parseHistories(): List<List<Int>> =
    map { it.split(" ").map { it.toInt() } }

fun main() {
    fun List<Int>.buildSeqs() = buildList {
        var prev = this@buildSeqs
        while (prev.any { it != 0 }) {
            add(prev)
            prev = List(prev.size - 1) { i -> prev[i + 1] - prev[i] }
        }
    }
    fun List<List<Int>>.sumOfFollowingValues(computeFollowing: (seq: List<Int>, acc: Int) -> Int) =
        sumOf { it.buildSeqs().foldRight(0,computeFollowing) }

    fun part1(hists: List<List<Int>>): Int =
        hists.sumOfFollowingValues { seq, acc -> seq.last() + acc }

    fun part2(hists: List<List<Int>>): Int =
        hists.sumOfFollowingValues { seq, acc -> seq.first() - acc }

    val testInput = readInput("Day09_test").parseHistories()
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09").parseHistories()
    println( measureTime { part1(input).println() } ) // 1762065988 - 10ms
    println( measureTime { part2(input).println() } ) // 1066 - 5ms
}
