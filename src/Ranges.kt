import kotlin.math.min
import kotlin.math.max

operator fun LongRange.times(other: LongRange): LongRange? {  // intersection
    val from = max(this.first, other.first)
    val to = min(this.last, other.last)
    return if (from<=to) from..to else null
}

operator fun LongRange.plus(other: LongRange): LongRange? = // union
    if (last !in other && first !in other) null
    else min(first, other.first)..max(last, other.last)

fun List<LongRange>.reduce(): List<LongRange> =
    if (size <= 1) this
    else buildList {
        for (r in this@reduce) {
            var u: LongRange? = null
            var idx = 0
            while (idx < size) {
                u = r + get(idx)
                if (u != null) break
                idx++
            }
            if (u != null) set(idx,u)
            else add(r)
        }
    }
