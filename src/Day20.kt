/* --- Advent of Code 2023 - Day 20: Pulse Propagation --- */

package day20

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

enum class NodeType(val symbol:Char?) { NONE(null), FLIP_FLOP('%'), CONJUNCTION('&') }

data class Node(val id: String, val outputs: List<String>, val type: NodeType)

fun Input.parseConfiguration(): Map<String,Node> = associate {
    val (name, out) = it.split(" -> ")
    val outputs = out.split(", ")
    val type = NodeType.entries.firstOrNull { name[0] == it.symbol } ?: NodeType.NONE
    val id = if (type != NodeType.NONE) name.drop(1) else name
    id to Node(id, outputs, type)
}

data class Pulse(val from: String, val to: String, val high: Boolean)

fun main() {
    fun part1(input: Input): Int {
        val nodes = input.parseConfiguration()
        val ffStates: MutableMap<String, Boolean> = nodes.filter { it.value.type == NodeType.FLIP_FLOP }
            .keys.associateWith { false }.toMutableMap()
        val cjStates: Map<String, MutableMap<String, Boolean>> = nodes.filter { it.value.type == NodeType.CONJUNCTION }
            .keys.associateWith { id ->
                buildMap { nodes.values.forEach { n -> n.outputs.forEach{ out -> if (out==id) put(n.id,false) } } }
                .toMutableMap()
            }
        var countHigh = 0
        var countLow = 0
        val pulses: MutableList<Pulse> = mutableListOf()
        fun processPulse() {
            pulses.add(Pulse("button","broadcaster", high = false))
            while (pulses.isNotEmpty()) {
                val pulse = pulses.removeFirst()
                //println("${pulse.from} -${if (pulse.high) "high" else "low"}-> ${pulse.to}")
                if (pulse.high) countHigh++ else countLow++
                val node = nodes[pulse.to] ?: continue
                val send = when (node.type) {
                    NodeType.FLIP_FLOP -> {
                        if (pulse.high) continue
                        (!ffStates.getValue(pulse.to)).also { ffStates[pulse.to] = it }
                    }
                    NodeType.CONJUNCTION -> {
                        cjStates.getValue(pulse.to)[pulse.from] = pulse.high
                        !cjStates.getValue(pulse.to).values.all { it }
                    }
                    else -> pulse.high
                }
                node.outputs.forEach { pulses.add(Pulse(pulse.to, it, send)) }
            }
        }
        repeat(1000) { processPulse() }
        return countLow * countHigh
    }

    fun part2(input: Input): Long {
        val nodes = input.parseConfiguration()
        val revLinks = nodes.values
            .flatMap { it.outputs }.distinct()
            .associateWith { out -> nodes.values.filter { out in it.outputs }.map { it.id } }
        val feeders = revLinks.getValue( revLinks.getValue("rx").first() )
            .flatMap { revLinks.getValue(it) }.toSet()
        //feeders.println()
        val res = nodes.getValue("broadcaster").outputs.map { start ->
            var cur = start
            val bits = StringBuilder()
            while (cur !in feeders) {
                //print("$cur->")
                val outs = nodes.getValue(cur).outputs
                val conj = outs.intersect(feeders)
                val ff = outs - feeders
                bits.insert(0,if (conj.isEmpty()) '0' else '1')
                cur = ff.firstOrNull() ?: conj.first()
            }
            //println("$cur : $bits")
            bits.toString().toLong(2)
        }.reduce(Long::times)
        return res
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 32000000)
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput2) == 11687500)
//
    val input = readInput("Day20")
    println( measureTime { part1(input).println() } ) // 825167435 - 25ms
    println( measureTime { part2(input).println() } ) // 225514321828633 - 5ms
}
