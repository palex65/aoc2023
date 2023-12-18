/* --- Advent of Code 2023 - Day 17: Clumsy Crucible --- */

package day17

import println
import readInput
import java.util.PriorityQueue
import kotlin.time.measureTime

typealias CityMap = List<List<Int>>

fun List<String>.parseMap(): CityMap = map { it.map { c -> c.digitToInt() } }

@JvmInline
value class Point(private val value: Int){
    val row get() = value shr 16
    val col get() = value and 0xFFFF
    override fun toString() = "($row,$col)"
    companion object {
        operator fun invoke(row: Int, col: Int) = Point(row shl 16 or col)
    }
}

fun Point.isValid(map: CityMap) = row in map.indices && col in map[0].indices
fun Point.move(dir: Direction) = Point(row+dir.dRow, col+dir.dCol)

enum class Direction(val dRow: Int, val dCol: Int) {
    UP(-1,0), RIGHT(0,+1), DOWN(+1,0), LEFT(0,-1)
}
val Direction.reverse get() = Direction.entries[(ordinal+2) % 4]

data class State(val pos: Point, val dir: Direction, val cost: Int, val consecutive: Int){
    override fun equals(other: Any?) = other is State && pos == other.pos && dir == other.dir && consecutive == other.consecutive
    override fun hashCode() = pos.hashCode() + dir.hashCode() + consecutive.hashCode()
}

inline fun leastHeatLoss(city: CityMap, nextDirs: (State)->List<Direction>, finalCondition: (State)->Boolean): Int {
    val open = PriorityQueue<State>(compareBy { it.cost })
    open.add( State(Point(1,0), Direction.DOWN, city[1][0], 1) )
    open.add( State(Point(0,1), Direction.RIGHT, city[0][1], 1) )
    val closed = mutableSetOf<State>()
    while (true) {
        val state = open.first()
        open.remove(state)
        if (finalCondition(state)) return state.cost
        if (state in closed) continue
        closed.add(state)
        val dirs = nextDirs(state)
        for(dir in dirs) {
            val pos = state.pos.move(dir).takeIf { it.isValid(city) } ?: continue
            val cost = state.cost + city[pos.row][pos.col]
            open.add( State(pos, dir, cost, if (dir == state.dir) state.consecutive + 1 else 1) )
        }
    }
}

fun main() {
    fun part1(city: CityMap): Int = leastHeatLoss(city,
        nextDirs = { state ->
            val dirs = Direction.entries - state.dir.reverse
            if (state.consecutive == 3) dirs - state.dir else dirs
        },
        finalCondition = { state -> state.pos == Point(city.size-1, city[0].size-1) }
    )

    fun part2(city: CityMap): Int = leastHeatLoss(city,
        nextDirs = { state ->
            if (state.consecutive < 4) listOf(state.dir)
            else {
                val dirs = Direction.entries - state.dir.reverse
                if (state.consecutive == 10) dirs - state.dir else dirs
            }
        },
        finalCondition = { state ->
            state.pos == Point(city.size-1, city[0].size-1) && state.consecutive >= 4
        }
    )

    val testInput = readInput("Day17_test").parseMap()
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)
    val test2 = readInput("Day17_test2").parseMap()
    check(part2(test2) == 71)
//
    val input = readInput("Day17").parseMap()
    println( measureTime { part1(input).println() } ) // 758 - 680ms
    println( measureTime { part2(input).println() } ) // 892 - 2.5s
}
