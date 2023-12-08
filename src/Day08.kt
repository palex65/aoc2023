/* --- Advent of Code 2023 - Day 8: Haunted Wasteland --- */

package day08

import println
import readInput
import kotlin.time.measureTime

enum class Dir(val symbol: Char){ LEFT('L'), RIGHT('R') }

sealed interface To {  // Sum type to (id: String | node: Node)
    val id: String get() = (this as ToNode).node.id
    val node: Node get() = error("Not a node")
}
class ToId(override val id: String): To
class ToNode(override val node: Node): To

class Node(val id: String, var left: To, var right: To) {
    override fun equals(other: Any?) = other === this
    override fun hashCode() = id.hashCode()
    override fun toString() = "Node($id, ${left.id}, ${right.id})"
}

data class Info(val dirs: String, val nodes: Map<String,Node>)

val reNode = Regex("""(\w+) = \((\w+), (\w+)\)""")

fun List<String>.parseInfo() = Info(
    dirs = first(),
        //.map { sym -> Dir.entries.first{ it.symbol==sym } },
    nodes = drop(2)
        .map {
            val (from,toLeft,toRight) = checkNotNull(reNode.matchEntire(it)?.destructured)
            Node(from, ToId(toLeft), ToId(toRight))
        }
        .associateBy { it.id }
        .resolveLinks()
)

fun Map<String,Node>.resolveLinks(): Map<String,Node> {
    values.forEach { node ->
        node.left = ToNode(getValue(node.left.id))
        node.right = ToNode(getValue(node.right.id))
    }
    return this
}

fun main() {
    fun pathLength(from: Node, targets: List<Node>, dirs: String): Int {
        var curr = from
        var steps = 0
        var idx = 0
        while (curr !in targets) {
            curr = if (dirs[idx] == 'L') curr.left.node else curr.right.node
            if (++idx == dirs.length) idx=0
            steps++
        }
        return steps
    }

    fun part1(input: List<String>): Int {
        val (dirs, nodes) = input.parseInfo()
        return pathLength(nodes.getValue("AAA"), listOf(nodes.getValue("ZZZ")), dirs)
    }

    fun lcm(a: Long, b: Long): Long { // Least Common Multiple using Euclid's algorithm
        var (x, y) = a to b
        while (y != 0L) {
            val t = y
            y = x % y
            x = t
        }
        return a * b / x
    }

    fun part2(input: List<String>): Long {
        val (dirs, nodes) = input.parseInfo()
        val targets = nodes.values.filter { it.id.last()=='Z' }
        return nodes.values
            .filter { it.id.last()=='A' }
            .map { pathLength(it,targets,dirs).toLong() }
            .reduce{ acc, i -> lcm(acc,i) }
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 6)
    val testInput2 = readInput("Day08_test2")
    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    println( measureTime { part1(input).println() } ) // 21797 - 15ms
    println( measureTime { part2(input).println() } ) // 23977527174353 - 15ms
}
