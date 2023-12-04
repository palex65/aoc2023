/* --- Advent of Code 2023 - Day 4: Scratchcards --- */

package day04

import println
import readInput

data class Card(
    val id: Int,
    val winning: List<Int>,
    val have: List<Int>,
    val prize: Int = have.count { it in winning }
)

fun String.parseCard(): Card {
    val (hdr, rest) = split(":")
    val id = hdr.drop("Card ".length).trim().toInt()
    val (winning, have) = rest.split(" | ")
        .map { it.split(" ")
            .filter{ it.isNotBlank() }
            .map { it.trim().toInt() }
        }
    return Card(id, winning, have)
}

fun Card.toPoints() = if (prize > 0) 1 shl (prize - 1) else 0

fun main() {
    fun part1(cards: List<Card>): Int = cards
        .sumOf { it.toPoints() }

    fun part2(cards: List<Card>): Int {
        val numOfEachCard = Array(cards.size) { 1 }
        for (card in cards) {
            repeat(card.prize) {
                val idx = card.id + it
                val n = numOfEachCard[card.id-1]
                if (idx < cards.size) numOfEachCard[idx]+= n
            }
        }
        return numOfEachCard.sum()
    }

    val testCards = readInput("Day04_test").map { it.parseCard() }
    check(part1(testCards) == 13)
    check(part2(testCards) == 30)

    val cards = readInput("Day04").map { it.parseCard() }
    part1(cards).println() // 26426
    part2(cards).println() // 6227972
}
