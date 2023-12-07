/* --- Advent of Code 2023 - Day 7: Camel Cards --- */

package day07

import println
import readInput
import kotlin.time.measureTime
import day07.Card.*
import day07.Type.*

enum class Card(val label: Char) {
    JOKER('?'), // Part2
    TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'),
    TEN('T'), JACK('J'), QUEEN('Q'), KING('K'), ACE('A')
}

data class Hand(val cards: List<Card>, val bid: Int)

fun Hand.compareByCard(other: Hand): Int {
    other.cards.forEachIndexed { idx, c2 ->
        val c1 = cards[idx]
        if (c1 != c2) return c1.ordinal - c2.ordinal
    }
    return 0
}

fun List<String>.parseCards(joker: Boolean= false): List<Hand> =
    map { it.split(" ") }.map { (cards, bid) ->
        Hand(
            cards.map { c ->
                if (joker && c == 'J') JOKER
                else Card.entries.first { it.label == c }
            },
            bid.toInt()
        )
    }

enum class Type {
    HIGH_CARD, ONE_PAIR, TWO_PAIRS, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

fun Hand.type(): Type =
    cards.groupingBy { it }.eachCount().let { counts ->
        when (counts.size) {
            5 -> HIGH_CARD
            4 -> ONE_PAIR
            3 -> if (counts.values.any { it == 3 }) THREE_OF_A_KIND else TWO_PAIRS
            2 -> if (counts.values.any { it == 4 }) FOUR_OF_A_KIND else FULL_HOUSE
            else -> FIVE_OF_A_KIND
        }
    }

val cardsWithoutJoker = Card.entries - JOKER

fun Hand.typeWithJoker(): Type {
    val numOfJokers = cards.count { it == JOKER }
    if (numOfJokers==0) return type()
    val fixedCards = cards.filter { it != JOKER }
    return cardsWithoutJoker.fold(HIGH_CARD){ res, card ->
        val type = copy(cards = fixedCards + List(numOfJokers){card}).type()
        if (type > res) type else res
    }
}

fun main() {
    fun computeWinnings(input: List<String>, joker: Boolean=false): Int = input
        .parseCards(joker)
        .groupBy { if (joker) it.typeWithJoker() else it.type() }
        .toSortedMap()
        .values.flatMap { it.sortedWith( Hand::compareByCard ) }
        .foldIndexed(0) { idx, acc, hand -> acc + hand.bid * (idx+1) }

    fun part1(input: List<String>) = computeWinnings(input)

    fun part2(input: List<String>) = computeWinnings(input, joker = true)

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    println( measureTime { part1(input).println() } ) // 253638586 - 25ms
    println( measureTime { part2(input).println() } ) // 253253225 - 30ms
}
