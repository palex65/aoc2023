/* --- Advent of Code 2023 - Day 15: Lens Library --- */

package day15

import println
import readInput
import kotlin.time.measureTime

fun List<String>.parseSteps(): List<String> =
    first().split(',')

fun String.hash(): Int = fold(0) { h, c -> (h + c.code) * 17 % 256 }

data class Lens(val label: String, val focal: Int)

fun main() {
    fun part1(steps: List<String>): Int = steps.sumOf { it.hash() }

    fun part2(steps: List<String>): Int {
        val boxes = List(256) { mutableListOf<Lens>() }
        steps.forEach { step ->
            val op = if ('=' in step) '=' else '-'
            val (label, focal) = step.split(op)
            val box = boxes[label.hash()]
            if (op == '=') {
                val lens = Lens(label, focal.toInt())
                val idx = box.indexOfFirst { it.label == label }
                if (idx == -1) box.add(lens) else box[idx] = lens
            } else
                box.removeIf { it.label == label }
        }
        return boxes.flatMapIndexed { box, lens -> lens.mapIndexed{ slot, each ->
            (box+1) * (slot+1) * each.focal
        } }.sum()
    }

    check("HASH".hash()==52)
    val testInput = readInput("Day15_test").parseSteps()
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15").parseSteps()
    println( measureTime { part1(input).println() } ) // 505427 - 2ms
    println( measureTime { part2(input).println() } ) // 243747 - 8ms
}
