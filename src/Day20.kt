/* --- Advent of Code 2023 - Day 20: Pulse Propagation --- */

package day20

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

enum class NodeType(val symbol:Char?) { NONE(null), FLIP_FLOP('%'), CONJUNCTION('&') }
val typeSymbols = NodeType.entries.map { it.symbol } - null

data class Node(val id: String, val outputs: List<String>, val type: NodeType)

//class FlipFlop(id: String, outputs: List<String>, var state: Boolean = false): Node(id,outputs)
//class Conjuntion(id: String, outputs: List<String>, val state: MutableMap<String,Boolean> = mutableMapOf()): Node(id,outputs)

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
        val ffStates = nodes.filter { it.value.type == NodeType.FLIP_FLOP }.keys.associateWith { false }.toMutableMap()
        val cjStates = nodes.filter { it.value.type == NodeType.CONJUNCTION }.keys.associateWith { id ->
            buildMap { nodes.values.forEach { n -> n.outputs.forEach{ out -> if (out==id) put(n.id,false) } } }.toMutableMap()
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
        repeat(1000) {
            processPulse()
        }
        //println("countLow = $countLow, countHigh = $countHigh")
        return countLow * countHigh
    }

    fun part2(input: Input): Int {
        val nodes = input.parseConfiguration()
        val ffStates = nodes.filter { it.value.type == NodeType.FLIP_FLOP }.keys.associateWith { false }.toMutableMap()
        val cjStates = nodes.filter { it.value.type == NodeType.CONJUNCTION }.keys.associateWith { id ->
            buildMap { nodes.values.forEach { n -> n.outputs.forEach{ out -> if (out==id) put(n.id,false) } } }.toMutableMap()
        }
        val pulses: MutableList<Pulse> = mutableListOf()
        fun processPulse(): Boolean {
            pulses.add(Pulse("button","broadcaster", high = false))
            while (pulses.isNotEmpty()) {
                val pulse = pulses.removeFirst()
                if (!pulse.high && pulse.to == "rx") return true
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
            return false
        }
        var count = 0
        do {
            count++
            if (cjStates["bn"]!!.values.any { it } || count % 1000000 == 0) {
                println("$count: bn.inputs=${cjStates["bn"]}")
                println(ffStates.values.joinToString("") { if (it) "1" else "0" }+" "+
                        cjStates.entries.joinToString("|") { "${it.key}:"+it.value.values.joinToString("") { if (it) "1" else "0" } }
                )
            }
        } while (!processPulse())
        return count
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 32000000)
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput2) == 11687500)
//    check(part2(testInput) == 1)
//
    val input = readInput("Day20")
    println( measureTime { part1(input).println() } ) // 0 - 1ms
    println( measureTime { part2(input).println() } ) // 1 - 50ms
}
