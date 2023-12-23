/* --- Advent of Code 2023 - Day 22: Sand Slabs --- */

package day22

import println
import readInput
import kotlin.time.measureTime

typealias Input = List<String>

data class Point(val x: Int, val y: Int, val z: Int)
data class Brick(val from: Point, val to: Point, val id: Int)

typealias Snapshot = List<Brick>

fun Input.parseSnapshot(): Snapshot = mapIndexed { idx, line ->
    val (p1,p2) = line.split("~").map { p ->
        p.split(',').map { it.toInt() }.let { (x,y,z) -> Point(x,y,z) }
    }
    Brick(p1,p2, idx)
}

data class MutableBrick(val points: List<Point>, val id: Int, val minZ: List<Point>, var offsetZ: Int = 0) {
    override fun equals(other: Any?) = other is MutableBrick && id == other.id
    override fun hashCode() = id.hashCode()
}

fun Brick.toMutableBrick(): MutableBrick {
    val points = buildList {
        for (x in from.x..to.x) for (y in from.y..to.y) for (z in from.z..to.z) add(Point(x, y, z))
    }
    return MutableBrick(points, id, points.filterMinZ())
}
fun List<Point>.filterMinZ() = sortedBy { it.z }.distinctBy { it.x to it.y }
fun List<Point>.filterMaxZ() = sortedBy { it.z }.reversed()//.distinctBy { it.x to it.y }

typealias Space = Array<Array<Array<MutableBrick?>>>
data class State(val bricks: List<MutableBrick>, val space: Space)

fun computeState(snapshot: Snapshot): State {
    val bricks = snapshot.sortedBy { it.to.z }.map { it.toMutableBrick() }
    val dimX = snapshot.maxOf { maxOf(it.from.x, it.to.x) } + 1
    val dimY = snapshot.maxOf { maxOf(it.from.y, it.to.y) } + 1
    val dimZ = snapshot.maxOf { maxOf(it.from.z, it.to.z) } + 1
    println("$dimX x $dimY x $dimZ")
    val space = Array(dimX) { Array(dimY) { Array<MutableBrick?>(dimZ){null} } }
    bricks.forEach { brick -> brick.points.forEach { space[it.x][it.y][it.z] = brick } }
    for(b in bricks) {
        while (b.minZ.all { it.z - b.offsetZ > 1 && space[it.x][it.y][it.z - 1 - b.offsetZ]==null }) {
            b.points.forEach { space[it.x][it.y][it.z - b.offsetZ] = null }
            b.offsetZ++
            b.points.forEach { space[it.x][it.y][it.z - b.offsetZ] = b }
        }
    }
    return State(bricks, space)
}

fun main() {
    fun supportedBy(brick: MutableBrick, space: Space) = //: List<MutableBrick> =
        brick.points.filterMaxZ().mapNotNull { space[it.x][it.y][it.z - brick.offsetZ + 1] }.toSet()-brick

    fun part1(input: Input): Int {
        val (bricks,space) = computeState(input.parseSnapshot())
        return bricks.count { b ->
            val supported = supportedBy(b, space)
            supported.all { bs -> bs.minZ.mapNotNull { space[it.x][it.y][it.z-bs.offsetZ-1] }.any { it != b } }
        }
    }

    fun part2(input: Input): Int {
        val (bricks,space) = computeState(input.parseSnapshot())
        val dp = Array(bricks.size) { mutableMapOf<Set<MutableBrick>,Int>() }
        fun countSupportedBy(brick: MutableBrick, fall: Set<MutableBrick> = emptySet()): Int {
            val cached = dp[brick.id][fall]
            if (cached!=null) return cached
            val supported = supportedBy(brick, space)
            return when {
                supported.isEmpty() -> fall.size
                supported.all { bs -> bs.minZ
                    .mapNotNull { space[it.x][it.y][it.z-bs.offsetZ-1] }
                    .all { it in fall }
                } -> supported.maxOf { countSupportedBy(it, fall + supported + brick) }
                else -> fall.size
            }. also { dp[brick.id][fall]=it }
        }
        return bricks.sumOf { countSupportedBy(it, setOf(it))-1 }
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)
//
    val input = readInput("Day22")
    println( measureTime { part1(input).println() } ) // 454 - 70ms
    println( measureTime { part2(input).println() } ) // 5369 - 50ms
}
