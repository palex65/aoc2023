
operator fun LongRange.times(other: LongRange): LongRange? {
    val from = kotlin.math.max(this.first, other.first)
    val to = kotlin.math.min(this.last, other.last)
    return if (from<=to) from..to else null
}

operator fun LongRange.plus(other: LongRange): LongRange? =
    if (last !in other && first !in other) null
    else kotlin.math.min(first, other.first)..kotlin.math.max(last, other.last)

fun List<LongRange>.reduce(): List<LongRange> {
    val res = mutableListOf<LongRange>()
    for (r in this) {
        val u = res.firstOrNull { r + it != null}
        if (u==null) res.add(r)
        else {
            res.remove(u)
            res.add((r+u)!!)
        }
    }
    //res.sortBy { it.first }
    //val total = res.sumOf { it.last- it.first + 1 }
    //println("$total: -> $res")
    return res
}
