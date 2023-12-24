/* --- Advent of Code 2023 - Day 23: A Long Walk --- */

package day23

import println
import readInput
import java.util.PriorityQueue
import kotlin.time.measureTime

typealias Input = List<String>

enum class Tile(val sym: Char) {
    PATH('.'), FOREST('#'),
    SLOPE_UP('^'), SLOPE_RIGHT('>'), SLOPE_DOWN('v'), SLOPE_LEFT('<')
}
enum class Direction(val sym: Char, val dRow: Int, val dCol: Int) {
    UP('^',-1,0), RIGHT('>',0,1), DOWN('v',1,0), LEFT('<',0,-1)
}
fun Direction.opposite() = Direction.entries[(ordinal+2)%4]

data class Point(val row: Int, val col: Int)
operator fun Point.plus(d: Direction) = Point(row+d.dRow, col+d.dCol)

typealias WalkMap = List<List<Tile>>

fun Input.parseMap(): WalkMap = map { ln -> ln.map { c -> Tile.entries.first { it.sym==c } } }

typealias DirectGraph = Map<Point,List<Point>>

fun WalkMap.toDirectGraph(start: Point, finish: Point, slopes: Boolean=false) = buildMap {
    put(start, listOf(start+ Direction.DOWN))
    put(finish, listOf(finish+ Direction.UP))
    val wm = this@toDirectGraph
    for(row in 1..wm.size-2) for(col in 1..wm[row].size-2) {
        val tile = wm[row][col]
        if (tile == Tile.FOREST) continue
        val pos = Point(row,col)
        put(pos, Direction.entries.mapNotNull { dir ->
            if (slopes && tile != Tile.PATH && tile.sym != dir.sym) null
            else (pos + dir).let { if (wm[it.row][it.col] == Tile.FOREST) null else it }
        })
    }
}

data class Link(val to: Point, val distance: Int)
typealias DistancesGraph = Map<Point,List<Link>>

fun DirectGraph.toDistanceGraph() = entries.filter { it.value.size != 2 }.associate { (from,to) ->
    from to to.map { p ->
        var f = from
        var t = p
        var distance = 1
        while (true) {
            val next = (this[t] ?: break) - f
            if (next.size != 1) break
            f = t
            t = next.single()
            distance++
        }
        Link(t, distance)
    }
}

fun DistancesGraph.computeMaxDistance(start: Point, finish: Point): Int {
    val visited = mutableSetOf<Point>()
    fun distanceFrom(from: Point): Int {
        if (from == finish) return 0
        visited += from
        return getValue(from).filter { it.to !in visited }
            .fold(-1) { acc, link ->
                val d = distanceFrom(link.to)
                if (d >= 0) maxOf(acc, d + link.distance) else acc
            }
            .also { visited -= from }
    }
    return distanceFrom(start)
}

fun main() {
    fun partN(input: Input, withSlopes: Boolean): Int {
        val wm = input.parseMap()
        val start = Point(0,1)
        val finish = Point(wm.size-1, wm[0].size-2)
        val directGraph = wm.toDirectGraph(start, finish, slopes=withSlopes)
        val distGraph = directGraph.toDistanceGraph()
        return distGraph.computeMaxDistance(start, finish)
    }

    fun part1(input: Input) = partN(input, withSlopes=true)

    fun part2(input: Input) = partN(input, withSlopes=false)

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    println( measureTime { part1(input).println() } ) // 2414 - 25ms
    println( measureTime { part2(input).println() } ) // 6598 - 3s
}
