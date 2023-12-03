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

    fun part1(games: List<Game>): Int = games
        .filter { game -> game.sets.all { it.red<=12 && it.green<=13 && it.blue<=14 } }
        .sumOf { it.id }

    fun part2(games: List<Game>): Long = games
        .map { Cubs(it.sets.maxOf { it.red }, it.sets.maxOf { it.green }, it.sets.maxOf { it.blue } ) }
        .sumOf { it.red.toLong() * it.green * it.blue }

    val testGames = readInput("Day02_test").map { it.parseGame() }
    check(part1(testGames) == 8)
    check(part2(testGames) == 2286L)

    val games = readInput("Day02").map { it.parseGame() }
    part1(games).println() // 2632
    part2(games).println() // 69629
}
