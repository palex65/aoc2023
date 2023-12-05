/* --- Advent of Code 2023 - Day 5: If You Give A Seed A Fertilizer --- */

package day05

import println
import readInput
import splitBy
import kotlin.math.min
import kotlin.time.measureTime

data class Range(val from: Long, val to: Long, val size: Long)
data class Map(val from: String, val to: String, val ranges: List<Range>)
data class SeedMaps(val seeds: List<Long>, val maps: List<Map>)

fun List<List<String>>.parseMaps(): SeedMaps {
    val seeds = first().single().substringAfter("seeds: ").split(' ').map { it.toLong() }
    val maps = drop(1).map { it.parseMap() }
    return SeedMaps(seeds, maps)
}

fun List<String>.parseMap(): Map {
    val (from, to) = first().substringBefore(" map:").split("-to-")
    val ranges = drop(1).map { it.parseRange() }.sortedBy { it.from }
    return Map(from, to, ranges)
}

fun String.parseRange(): Range {
    val (to, from, size) = trim().split(' ').map { it.toLong() }
    return Range(from, to, size)
}

fun Map.convert(from: Long): Long {
    val range = ranges.firstOrNull { from in it.from..<(it.from+it.size) } ?: return from
    return range.to + (from - range.from)
}

typealias RangeList = List<LongRange>

val Range.delta: Long get() = to - from

fun Map.convert(from: RangeList): RangeList {
    val res = mutableListOf<LongRange>()
    for(r in from) {
        var first = r.first
        var last = r.last
        while (first<=last) {
            val fr = ranges.firstOrNull { first in it.from..<(it.from+it.size) }
            if (fr!=null) {
                res.add(first+fr.delta..min(fr.to + fr.size - 1, last + fr.delta))
                first = fr.from+fr.size
            } else {
                val lr = ranges.firstOrNull { last in it.from..<(it.from+it.size) }
                if (lr!=null) {
                    res.add(lr.to..min(lr.to + lr.size - 1, last + lr.delta))
                    last = lr.from-1
                } else {
                    val rs = ranges.firstOrNull { it.from in first..last }
                    if (rs!=null) {
                        res.add(rs.to..<rs.to+rs.size)
                        res.add(first..<rs.from)
                        first = rs.from+rs.size
                    } else {
                        res.add(first..last)
                        break
                    }
                }
            }
        }
    }
    return res
}

fun main() {
    fun part1(maps: SeedMaps) = maps.maps
        .fold(maps.seeds) { values, map ->
            values.map { map.convert(it) }
        }
        .minOrNull() ?: error("no min")

    fun part2(maps: SeedMaps): Long {
        val seeds = buildList {
            for (i in 0..<maps.seeds.size step 2)
                add(maps.seeds[i]..<maps.seeds[i] + maps.seeds[i + 1])
        }
        return maps.maps
            .fold(seeds) { values, map ->
                map.convert(values)
            }
            .minOf { it.first }
    }

    val testMaps = readInput("Day05_test").splitBy { it.isBlank() }.parseMaps()
    check(part1(testMaps) == 35L)
    check(part2(testMaps) == 46L)

    val maps = readInput("Day05").splitBy { it.isBlank() }.parseMaps()
    println( measureTime { part1(maps).println() } ) // 165788812 - 2ms
    println( measureTime { part2(maps).println() } ) // 1928058 - 2ms
}
