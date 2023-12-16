/* --- Advent of Code 2023 - Day 16: The Floor Will Be Lava --- */

package day16

import println
import readInput
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.time.measureTime

enum class Direction(val sym: Char, val mask: Byte, val dRow: Int, val dCol: Int) {
    UP('^',0b0001,-1,0), RIGHT('>',0b0010,0,+1), DOWN('v',0b0100,+1,0), LEFT('<',0b1000,0,-1)
}
const val byte0 = 0.toByte()

fun Direction.isHorizontal() = this==Direction.LEFT || this==Direction.RIGHT
fun Direction.turnLeft(): Direction = Direction.entries[(ordinal+3) % 4]
fun Direction.turnRight(): Direction = Direction.entries[(ordinal+1) % 4]
fun Byte.dirToChar(): Char = if (this== byte0) '.'
    else Direction.entries.firstOrNull { it.mask == this }?.sym ?: this.countOneBits().digitToChar()

enum class Tile(val sym: Char) {
    MIRROR_WN_ES('/'), MIRROR_EN_WS('\\'), DIVIDER_NS('|'), DIVIDER_EW('-')
}

fun Tile.outDir(inDir: Direction): List<Direction> = when(this) {
    Tile.MIRROR_WN_ES -> listOf( if (inDir.isHorizontal()) inDir.turnLeft() else inDir.turnRight() )
    Tile.MIRROR_EN_WS -> listOf( if (inDir.isHorizontal()) inDir.turnRight() else inDir.turnLeft() )
    Tile.DIVIDER_NS -> if (inDir.isHorizontal()) listOf(Direction.UP, Direction.DOWN) else listOf(inDir)
    Tile.DIVIDER_EW -> if (inDir.isHorizontal()) listOf(inDir) else listOf(Direction.LEFT, Direction.RIGHT)
}

typealias GridTiles = List<List<Tile?>>
val GridTiles.height: Int get() = size
val GridTiles.width: Int get() = get(0).size
operator fun GridTiles.get(pos: Point): Tile? = get(pos.row)[pos.col]

fun List<String>.parseGrid(): GridTiles = map { line ->
    line.map { c -> Tile.entries.firstOrNull { it.sym==c } }
}

data class Point(val row: Int, val col: Int)
fun Point.move(dir: Direction) = Point(row+dir.dRow, col+dir.dCol)

operator fun GridTiles.contains(pos: Point): Boolean =
    pos.row in 0..<height && pos.col in 0..<width

data class State(val pos: Point, val dir: Direction)

typealias GridDirs = List<ByteArray>
operator fun GridDirs.get(pos: Point) = get(pos.row)[pos.col]
operator fun GridDirs.set(pos: Point, value: Byte) { get(pos.row)[pos.col] = value }
fun GridDirs.print() = forEach { line ->
    line.forEach { print(it.dirToChar()) }
    println("")
}

fun GridTiles.energizedForEdge(edge: State): Int {
    val gridDirs = map { line -> ByteArray(line.size) }
    var state = listOf(edge)
    while( state.isNotEmpty() ) {
        state = state.mapNotNull { (pos, dir) ->
            if (pos !in this || (gridDirs[pos] and dir.mask) != byte0) null
            else {
                gridDirs[pos] = gridDirs[pos] or dir.mask
                val outDirs = this[pos]?.outDir(dir) ?: listOf(dir)
                outDirs.map { State(pos.move(it), it) }
            }
        }.flatten()
    }
    return gridDirs.sumOf { line -> line.count{ it > 0 } }
}

fun main() {
    fun part1(grid: GridTiles): Int =
        grid.energizedForEdge(State(Point(0,0), Direction.RIGHT))

    fun part2(grid: GridTiles): Int {
        val edges = List(grid.width) { col -> State(Point(0,col), Direction.DOWN) } +
                List(grid.width) { col -> State(Point(grid.height-1,col), Direction.UP) } +
                List(grid.height) { row -> State(Point(row,0), Direction.RIGHT) } +
                List(grid.height) { row -> State(Point(row,grid.width-1), Direction.LEFT) }
        return edges.maxOf { grid.energizedForEdge(it) }
    }

    val testInput = readInput("Day16_test").parseGrid()
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)
//
    val input = readInput("Day16").parseGrid()
    println( measureTime { part1(input).println() } ) // 7199 - 10ms
    println( measureTime { part2(input).println() } ) // 7438 - 460ms
}
