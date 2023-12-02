fun main() {
    data class Cubs(val red: Int, val green: Int, val blue: Int)
    data class Game(val id: Int, val sets: List<Cubs>)

    fun List<Pair<Int,String>>.get(color: String) = find { it.second == color }?.first ?: 0

    fun String.parseCubs() = split(", ")
            .map { it.split(' ') }
            .map { (n,c) -> n.toInt() to c }
            .let { Cubs(it.get("red"), it.get("green"), it.get("blue")) }

    fun String.parseSets() = split("; ").map { it.parseCubs() }
    fun String.parseGame() = split(": ").let { (game, sets) ->
        Game(game.drop("Game ".length).toInt(), sets.parseSets())
    }

    fun part1(input: List<String>): Int = input
        .map { it.parseGame() }
        .filter { game -> game.sets.all { it.red<=12 && it.green<=13 && it.blue<=14 } }
        .sumOf { it.id }

    fun part2(input: List<String>): Long = input
        .map { it.parseGame() }
        .map { Cubs(it.sets.maxOf { it.red }, it.sets.maxOf { it.green }, it.sets.maxOf { it.blue } ) }
        .sumOf { it.red.toLong() * it.green * it.blue }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286L)

    val input = readInput("Day02")
    part1(input).println() // 2632
    part2(input).println() // 55093
}
