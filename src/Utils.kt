import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Splits a list into multiple lists separated by the elements that match the predicate.
 */
fun <T> List<T>.splitBy( predicate: (T)->Boolean ): List<List<T>> {
    val res = mutableListOf<List<T>>()
    var from = 0
    var to = 0
    while (to < size) {
        while(to < size && !predicate(this[to])) ++to
        res.add(subList(from, to))
        from = ++to
    }
    return res
}
