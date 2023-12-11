/* --- Advent of Code 2023 - Day 10: Pipe Maze --- */

package day10

import readInput
import day10.Direction.*
import println
import kotlin.time.measureTime

enum class Direction(val dRow: Int, val dCol: Int) {
    N(-1, 0), E(0, 1), S(1, 0), W(0, -1)
}
val Direction.opposite get() = entries[(ordinal+2) % entries.size]
val Direction.right get() = entries[(ordinal+1) % entries.size]
val Direction.left get() = entries[(ordinal-1+ entries.size) % entries.size]

val allDirections = Direction.entries

data class Pos(val row: Int, val col: Int)

operator fun Pos.plus(dir: Direction): Pos = Pos(row + dir.dRow, col + dir.dCol)

enum class Pipe(val sym: Char, val dir1: Direction, val dir2: Direction, val sym2: Char=sym) {
    HORIZONTAL('-',E, W,'━'),   VERTICAL('|',N, S,'┃'),
    TOP_LEFT('F',S, E,'┏'),     TOP_RIGHT('7',S, W,'┓'),
    BOTTOM_LEFT('L',N, E,'┗'),  BOTTOM_RIGHT('J',N, W,'┛'),
    START('S',N, N)
}

fun Char.toPipe(): Pipe? = Pipe.entries.firstOrNull { it.sym == this }

fun Pipe.dirTo(pos: Pos, to: Pos): Direction? = when (to) {
    pos + dir1 -> dir1
    pos + dir2 -> dir2
    else -> null
}

fun Pipe.otherDir(that: Direction) = if (that==dir1) dir2 else dir1

typealias PipeMap = List<List<Pipe?>>

operator fun PipeMap.get(pos: Pos): Pipe? = getOrNull(pos.row)?.getOrNull(pos.col)

fun PipeMap.posOfFirst( predicate: (pipe: Pipe?, pos: Pos) -> Boolean): Pos? {
    forEachIndexed { r, line ->
        line.forEachIndexed { c, pipe -> Pos(r,c).let { if (predicate(pipe,it)) return it } }
    }
    return null
}

fun PipeMap.posOf(pipe: Pipe): Pos =
    posOfFirst { p, _ -> pipe==p } ?: error("Pipe $pipe not found")

fun List<String>.parsePipeMap(): PipeMap =
    map { it.map { c -> c.toPipe() } }

data class State(val pos: Pos, val dir: Direction)

// ---- Part 2 ------
enum class Mark(val sym: Char) {
    Inside('I'), Outside('O'), LOOP('#')
}

typealias MarkMap = List<Array<Mark?>>

operator fun MarkMap.get(pos: Pos): Mark? = getOrNull(pos.row)?.getOrNull(pos.col)
operator fun MarkMap.set(pos: Pos, mark: Mark) { getOrNull(pos.row)?.set(pos.col, mark) }

fun MarkMap.isValidPos(pos: Pos) = pos.row in indices && pos.col in get(0).indices

fun printMap(markMap: List<Array<Mark?>>, pipes: PipeMap, curr: Pos?=null) {
    markMap.forEachIndexed { row, line ->
        line.forEachIndexed { col, mark ->
            val pos = Pos(row, col)
            val sym =
                if (pos==curr) 'X'
                else when (mark) {
                    Mark.LOOP -> pipes[pos]?.sym2 ?: '?'
                    null -> '#'
                    else -> mark.sym
                }
            print(sym)
        }
        println()
    }
}

fun MarkMap.markLoop(pipes: PipeMap) {
    val start = pipes.posOf(Pipe.START)
    this[start] = Mark.LOOP
    var state = allDirections.mapNotNull {
        val pos = start + it
        val pipe = pipes[pos] ?: return@mapNotNull null
        val dir = pipe.dirTo(pos, start) ?: return@mapNotNull null
        State(pos, pipe.otherDir(dir))
    }.first()
    while (true) {
        this[state.pos] = Mark.LOOP
        val next = state.pos + state.dir
        if (next == start) break
        val pipe = checkNotNull(pipes[next])
        state = State(next, pipe.otherDir(state.dir.opposite))
    }
}

fun MarkMap.markBounds() {
    val width = get(0).size
    val height = size
    var bounds = buildList {
        repeat(height) { add(Pos(it,0)); add(Pos(it,width-1)) }
        repeat(width-2) { add(Pos(0,it+1)); add(Pos(size-1,it+1)) }
    }.filter{ get(it) == null }
    while (bounds.isNotEmpty()) {
        bounds.forEach { this[it] = Mark.Outside }
        val adjacent = bounds.flatMap { pos -> allDirections.map { pos+it } }.toSet()
        bounds = adjacent.filter { isValidPos(it) && get(it)==null }
    }
}

fun MarkMap.markInside(pipes: PipeMap) {
    //printMap(this,pipes)
    val first = pipes.posOfFirst { _, pos ->
        get(pos)==Mark.LOOP && allDirections.any { get(pos + it) == Mark.Outside }
    } ?: error("No loop found")
    markInDirection(first, pipes[first]!!.dir1, pipes)
    markInDirection(first, pipes[first]!!.dir2, pipes)
}

fun MarkMap.markRemainders(pipes: PipeMap) {
    while(true) {
        val pos = pipes.posOfFirst { _, pos -> get(pos)==null } ?: break
        val adjacent = allDirections.map { pos+it }.toMutableSet()
        adjacent.add(pos)
        while (adjacent.all { get(it) == null }) {
            adjacent.addAll( adjacent.flatMap{ allDirections.map { pos+it } } )
        }
        val mark = adjacent.firstNotNullOf { get(it) }
        check( adjacent.mapNotNull { get(it) }.toSet().size==1 )
        adjacent.forEach { set(it,mark) }
        //printMap(this,pipes,pos)
    }
}

fun MarkMap.markInDirection(first: Pos, firstDir: Direction, pipes: PipeMap) {
    var pos: Pos = first
    var outside = allDirections.first { dir -> (first + dir).let { !isValidPos(it) || get(it) == Mark.Outside } }
    var dir = firstDir
    while (pipes[pos] != Pipe.START) {
        val p1 = pos + outside
        if (isValidPos(p1) && get(p1) == null) set(p1, Mark.Outside)
        val pipe = checkNotNull(pipes[pos])
        val otherSides = allDirections.filter { it != pipe.dir1 && it != pipe.dir2 && it != outside }
        otherSides.forEach {
            val p2 = pos + it
            if (isValidPos(p2) && get(p2) == null) set(p2, when (pipe) {
                Pipe.HORIZONTAL, Pipe.VERTICAL -> Mark.Inside.also { check(outside != pipe.dir1 && outside != pipe.dir2) }
                else -> if (outside == pipe.dir1 || outside == pipe.dir2) Mark.Inside else Mark.Outside
            })
        }
        val oldPos = pos
        pos += dir
        val next = checkNotNull(pipes[pos]) {"Null pipe in $pos old=$oldPos".also {
            printMap(this,pipes,oldPos)
        }}
        if (next == Pipe.START) break
        outside = when (pipe) {
            Pipe.VERTICAL, Pipe.HORIZONTAL -> outside
            Pipe.TOP_LEFT -> if (dir == E) when (outside) { W, N -> N; else -> S }
                             else when (outside) { W, N -> W; else -> E }
            Pipe.TOP_RIGHT -> if (dir == W) when (outside) { E, N -> N; else -> S }
                              else when (outside) { E, N -> E; else -> W }
            Pipe.BOTTOM_LEFT -> if (dir == N) when (outside) { W, S -> W; else -> E }
                                else when (outside) { W, S -> S; else -> N }
            Pipe.BOTTOM_RIGHT -> if (dir == N) when (outside) { E, S -> E; else -> W }
                                 else when (outside) { E, S -> S; else -> N }
            else -> error("START")
        }
        dir = next.otherDir(dir.opposite)
    }
}

fun main() {
    fun part1(pipes: PipeMap): Int {
        val start: Pos = pipes.posOf(Pipe.START)
        var state: List<State> = allDirections.mapNotNull {
            val pos = start + it
            val pipe = pipes[pos] ?: return@mapNotNull null
            val dir = pipe.dirTo(pos,start) ?: return@mapNotNull null
            State(pos, pipe.otherDir(dir))
        }
        check(state.size==2)
        var steps = 1
        do {
            steps++
            state = state.map { (pos, dir) ->
                val next = pos + dir
                val pipe = checkNotNull(pipes[next])
                State(next, pipe.otherDir(dir.opposite))
            }
        } while (state[0].pos != state[1].pos)
        return steps
    }

    fun part2(pipes: PipeMap): Int {
        val markMap = pipes.map { Array<Mark?>(it.size) { null } }
        markMap.markLoop(pipes)
        markMap.markBounds()
        markMap.markInside(pipes)
    //    printMap(markMap, pipes)
        markMap.markRemainders(pipes)
    //    printMap(markMap, pipes)
        return markMap.sumOf { it.count { it == Mark.Inside } }
    }

    val testInput = readInput("Day10_test").parsePipeMap()
    check(part1(testInput) == 8)
    val test2Input = readInput("Day10_test2").parsePipeMap()
    check(part2(test2Input) == 10)
//
    val input = readInput("Day10").parsePipeMap()
    println( measureTime { part1(input).println() } ) // 7005 - 15ms
    println( measureTime { part2(input).println() } ) // 417 - 100ms
}
