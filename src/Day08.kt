/* --- Advent of Code 2023 - Day 8: Haunted Wasteland --- */

package day08

import println
import readInput
import kotlin.time.measureTime

sealed interface To {  // Sum type to (id: String | node: Node)
    val id: String get() = (this as To.No).node.id
    val node: Node get() = error("Not a node")
    class Id(override val id: String): To
    class No(override val node: Node): To
}

class Node(val id: String, var left: To, var right: To) {
    override fun toString() = "Node($id, ${left.id}, ${right.id})"
}

data class Info(val dirs: String, val nodes: Map<String,Node>)

val reNode = Regex("""(\w+) = \((\w+), (\w+)\)""")

fun List<String>.parseInfo() = Info(
    dirs = first(),
    nodes = drop(2)
        .map {
            val (from,toLeft,toRight) = checkNotNull(reNode.matchEntire(it)?.destructured)
            Node(from, To.Id(toLeft), To.Id(toRight))
        }
        .associateNodes()
)

fun List<Node>.associateNodes(): Map<String,Node> =
    associateByTo( LinkedHashMap(size) ) { it.id }.also { map ->
        map.values.forEach { node ->
            node.left = To.No(map.getValue(node.left.id))
            node.right = To.No(map.getValue(node.right.id))
        }
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

    tailrec fun gcd(a: Long, b: Long): Long = // Greatest Common Divisor (Euclid's algorithm)
        if (a == 0L) b else gcd(b % a, a)

    fun lcm(a: Long, b: Long): Long = // Least Common Multiple
        a * b / gcd(a, b)

    fun part2(input: List<String>): Long {
        val (dirs, nodes) = input.parseInfo()
        val targets = nodes.values.filter { it.id.last()=='Z' }
        return nodes.values
            .filter { it.id.last()=='A' }
            .map { pathLength(it,targets,dirs).toLong() }
            .reduce { acc, i -> lcm(acc,i) }
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 6)
    val testInput2 = readInput("Day08_test2")
    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    println( measureTime { part1(input).println() } ) // 21797 - 15ms
    println( measureTime { part2(input).println() } ) // 23977527174353 - 15ms
}
