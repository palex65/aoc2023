/* --- Advent of Code 2023 - Day 25: Snowverload --- */

package day25

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

typealias Graph = Map<String, List<String>>

fun Input.parseGraph(): Graph {
    val graph = mutableMapOf<String, MutableList<String>>()
    for (line in this) {
        val (component, connected) = line.split(": ")
        val connectedComponents = connected.split(" ")
        graph.getOrPut(component) { mutableListOf() } += connectedComponents
        connectedComponents.forEach {
            graph.getOrPut(it) { mutableListOf() } += component
        }
    }
    return graph
}

@JvmInline
value class Group private constructor(private val bits: BooleanArray) {
    val size : Int get() = bits.count { it }
    constructor(dim: Int, vararg elements: Int): this(BooleanArray(dim)) { elements.forEach { bits[it] = true } }
    operator fun contains(i: Int) = bits[i]
    operator fun plus(i: Int) = Group(bits.copyOf().apply { this[i] = true })
    operator fun minus(i: Int) = Group(bits.copyOf().apply { this[i] = false })
    operator fun plusAssign(i: Int) { bits[i] = true }
    operator fun minusAssign(i: Int) { bits[i] = false }
    fun sumOf(f: (Int) -> Int) = bits.mapIndexed { i, b -> if (b) f(i) else 0 }.sum()
    fun <T> map(f: (Int) -> T) = bits.mapIndexed { i, b -> if (b) f(i) else null }.filterNotNull()
}
//typealias Group = Set<Int>

fun main() {
    fun part1(input: Input): Int {
        val graph = input.parseGraph()
        val nodes: List<String> = graph.keys.toList()
        val links: List<List<Int>> = graph.values.map { it.map { name -> nodes.indexOf(name) } }

        fun Group.numberOfLinksTo(node: Int) = links[node].count { it in this }
        fun Group.numberOfLinksTo(g: Group) = sumOf { g.numberOfLinksTo(it) }
        fun Group.bestNodeToOut() = map { numberOfLinksTo(it) to it }.minBy { it.first }.second

        val group1 = Group(nodes.size,0)
        val group2 = Group(nodes.size, *(nodes.indices - 0).toIntArray())
//        val group1 = mutableSetOf(0)
//        val group2 = (nodes.indices - 0).toMutableSet()
        var linksBetween = group1.numberOfLinksTo(group2)
        var binary = false
        while (linksBetween>3) {
            val n1 = group2.bestNodeToOut()
            val lb1 = (group1 + n1).numberOfLinksTo(group2 - n1)
            if (group1.size>=nodes.size/2) binary = true
            if (binary) {
                val n2 = group1.bestNodeToOut()
                val lb2 = (group1 - n2).numberOfLinksTo(group2 + n2)
                if (lb1 > lb2) {
                    group1 -= n2; group2 += n2; linksBetween = lb2
                    continue
                }
            }
            group1 += n1; group2 -= n1; linksBetween = lb1
        }
        return group1.size * group2.size
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54)

    val input = readInput("Day25")
    println( measureTime { part1(input).println() } ) // 558376 - (class Group - 140ms; typealias Set<Int> - 170ms)
}
