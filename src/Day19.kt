/* --- Advent of Code 2023 - Day 19: Aplenty --- */

package day19

import println
import readInput
import splitBy
import java.util.concurrent.locks.Condition
import kotlin.time.measureTime

typealias Input = List<String>

enum class Category(val letter: Char) { X('x'), M('m'), A('a'), S('s') }
enum class Operation(val text: String, val validator: (rating: Int, limit:Int)->Boolean) {
    LESS("<", { rating, limit -> rating < limit }),
    GREATER(">", { rating, limit -> rating > limit }),
}

data class Part(val rating: Map<Category,Int>)
data class Rule(val category: Category, val operation: Operation, val limit: Int, val to: String)
data class Workflow(val id: String, val rules: List<Rule>, val default: String)

fun String.parseWorkflow(): Workflow {
    val id = substringBefore('{')
    val desc = substringAfter('{').substringBefore('}').split(',')
    val rules = desc.dropLast(1).map { line ->
        val (cat, limit, to) = line.split("<", ">", ":")
        val op = line.substringAfter(cat).substringBefore(limit)
        Rule(
            Category.entries.first { it.letter == cat.first() },
            Operation.entries.first { it.text == op },
            limit.toInt(),
            to
        )
    }
    return Workflow(id, rules, desc.last())
}

fun String.parsePart() = Part(
    removeSurrounding("{","}").split(',').map {
        val (cat, value) = it.split("=")
        Category.entries.first { it.letter == cat.first() } to value.toInt()
    }.toMap()
)

typealias Workflows = Map<String, Workflow>
typealias Ranges = Map<Category, IntRange>

fun main() {
    fun Workflow.process(part: Part): String {
        for (rule in rules) {
            val rating = part.rating[rule.category] ?: error("No rating for ${rule.category}")
            if (rule.operation.validator(rating, rule.limit)) return rule.to
        }
        return default
    }
    fun Part.isAccepted(wfs: Workflows): Boolean {
        var wf = wfs["in"] ?: error("No workflow for 'in'")
        while (true) {
            val to = wf.process(this)
            if (to == "A") return true
            if (to == "R") return false
            wf = wfs[to] ?: error("No workflow for '$to'")
        }
    }

    fun part1(input: Input): Int {
        val (w, p) = input.splitBy { it.isEmpty() }
        val workflows = w.map { it.parseWorkflow() }.associateBy { it.id }
        val parts = p.map { it.parsePart() }
        val ratings = parts.filter { it.isAccepted(workflows) }.map { it.rating.values.sum() }
        return ratings.sum()
    }

    fun part2(input: Input): Long {
        val workflows = input.splitBy { it.isEmpty() }.first().map { it.parseWorkflow() }.associateBy { it.id }
        fun process(wfId: String, ranges: Ranges): Long {
            if (wfId == "R") return 0L
            if (wfId == "A") return ranges.values.fold(1L) { acc, range -> acc * range.count() }
            val wf = workflows.getValue(wfId)
            val newRanges = ranges.toMutableMap()
            var count = 0L
            for (rule in wf.rules) {
                val range = newRanges.getValue(rule.category)
                val positiveRange = when (rule.operation) {
                    Operation.LESS -> range.first..minOf(rule.limit-1, range.last)
                    Operation.GREATER -> maxOf(rule.limit + 1,range.first)..range.last
                }
                newRanges[rule.category] = positiveRange
                count += process(rule.to, newRanges)
                val negativeRange = when (rule.operation) {
                    Operation.LESS -> maxOf(rule.limit, range.first)..range.last
                    Operation.GREATER -> range.first..minOf(rule.limit, range.last)
                }
                newRanges[rule.category] = negativeRange
            }
            count += process(wf.default, newRanges)
            return count
        }
        return process("in", Category.entries.associateWith { (1..4000) })
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)
//
    val input = readInput("Day19")
    println( measureTime { part1(input).println() } ) // 575412 - 20ms
    println( measureTime { part2(input).println() } ) // 126107942006821 - 25ms
}
