/* --- Advent of Code 2023 - Day 19: Aplenty --- */

package day19

import println
import readInput
import splitBy
import kotlin.time.measureTime

typealias Input = List<String>

enum class Category(val letter: Char) { X('x'), M('m'), A('a'), S('s') }
enum class Operation(
    val text: String,
    val validator: (rating: Int, limit:Int)->Boolean,
    val thenRange: (limit: Int, range: IntRange)->IntRange,
    val elseRange: (limit: Int, range: IntRange)->IntRange
) {
    LESS("<",
        validator = { rating, limit -> rating < limit },
        thenRange = { limit, range -> range.first..minOf(limit - 1, range.last) },
        elseRange = { limit, range -> maxOf(limit, range.first)..range.last }
    ),
    GREATER(">",
        validator = { rating, limit -> rating > limit },
        thenRange = { limit, range -> maxOf(limit + 1, range.first)..range.last },
        elseRange = { limit, range -> range.first..minOf(limit, range.last) }
    ),
}

data class Part(val ratings: List<Int>)
fun Part.getRating(cat: Category) = ratings[cat.ordinal]

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
    removeSurrounding("{", "}").split(',').map { pair ->
        val (cat, value) = pair.split("=")
        Category.entries.first { it.letter == cat.first() } to value.toInt()
    }.sortedBy { it.first.ordinal }.map { it.second }
)

typealias Workflows = Map<String, Workflow>

typealias Ranges = List<IntRange>
fun Ranges(init: (Int)->IntRange): Ranges = List(Category.entries.size, init)
operator fun Ranges.get(cat: Category) = this[cat.ordinal]
fun Ranges.combinations() = fold(1L) { acc, range -> acc * range.count() }
fun Ranges.replace(cat: Category, range: IntRange) = Ranges { if (it == cat.ordinal) range else this[it] }
//operator fun Ranges.set(cat: Category, range: IntRange) { this[cat.ordinal] = range }

fun main() {
    fun Workflow.process(part: Part) =
        rules.firstOrNull { rule ->
            rule.operation.validator(part.getRating(rule.category), rule.limit)
        }?.to ?: default

    fun Part.isAccepted(wfs: Workflows): Boolean {
        var wf = wfs.getValue("in")
        while (true) {
            val to = wf.process(this)
            if (to == "A") return true
            if (to == "R") return false
            wf = wfs.getValue(to)
        }
    }

    fun part1(input: Input): Int {
        val (w, p) = input.splitBy { it.isEmpty() }
        val workflows = w.map { it.parseWorkflow() }.associateBy { it.id }
        val parts = p.map { it.parsePart() }
        val ratings = parts.filter { it.isAccepted(workflows) }.map { it.ratings.sum() }
        return ratings.sum()
    }

    fun part2(input: Input): Long {
        val workflows = input.splitBy { it.isEmpty() }.first().map { it.parseWorkflow() }.associateBy { it.id }
        fun combinations(wfId: String, ranges: Ranges): Long = when(wfId) {
            "R" -> 0L
            "A" -> ranges.combinations()
            else -> with( workflows.getValue(wfId) ) {
                var rgs = ranges
                var count = 0L
                for (rule in rules) {
                    val rg = rgs[rule.category]
                    count += combinations(rule.to, rgs.replace(rule.category, rule.operation.thenRange(rule.limit, rg)))
                    rgs = rgs.replace(rule.category, rule.operation.elseRange(rule.limit, rg))
                }
                count += combinations(default, rgs)
                count
            }
        }
        return combinations("in", Ranges { (1..4000) })
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)
//
    val input = readInput("Day19")
    println( measureTime { part1(input).println() } ) // 575412 - 20ms
    println( measureTime { part2(input).println() } ) // 126107942006821 - 25ms
}
