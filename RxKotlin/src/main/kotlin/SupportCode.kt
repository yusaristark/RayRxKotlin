import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.awt.Label
import java.util.Random

fun exampleOf(description: String, action: () -> Unit) {
    println("\n--- Example of: $description ---")
    action()
}

fun <T> printWithLabel(label: String, element: T) {
    println("$label $element")
}

val cards = mutableListOf(
    Pair("🂡", 11), Pair("🂢", 2), Pair("🂣", 3), Pair("🂤", 4), Pair("🂥", 5), Pair("🂦", 6), Pair("🂧", 7),
    Pair("🂨", 8), Pair("🂩", 9), Pair("🂪", 10), Pair("🂫", 10), Pair("🂭", 10), Pair("🂮", 10),
    Pair("🂱", 11), Pair("🂲", 2), Pair("🂳", 3), Pair("🂴", 4), Pair("🂵", 5), Pair("🂶", 6), Pair("🂷", 7),
    Pair("🂸", 8), Pair("🂹", 9), Pair("🂺", 10), Pair("🂻", 10), Pair("🂽", 10), Pair("🂾", 10),
    Pair("🃁", 11), Pair("🃂", 2), Pair("🃃", 3), Pair("🃄", 4), Pair("🃅", 5), Pair("🃆", 6), Pair("🃇", 7),
    Pair("🃈", 8), Pair("🃉", 9), Pair("🃊", 10), Pair("🃋", 10), Pair("🃍", 10), Pair("🃎", 10),
    Pair("🃑", 11), Pair("🃒", 2), Pair("🃓", 3), Pair("🃔", 4), Pair("🃕", 5), Pair("🃖", 6), Pair("🃗", 7),
    Pair("🃘", 8), Pair("🃙", 9), Pair("🃚", 10), Pair("🃛", 10), Pair("🃝", 10), Pair("🃞", 10)
)

fun cardString(hand: List<Pair<String, Int>>): String {
    return hand.joinToString("") { it.first }
}

fun points(hand: List<Pair<String, Int>>) = hand.map { it.second }.fold(0) { s, a -> s + a }

fun IntRange.random() = Random().nextInt(endInclusive - start) +  start

sealed class HandError: Throwable() {
    class Busted: HandError()
}

fun String.romanNumeralIntValue(): Int {
    return when (this) {
        "I" -> 1
        "V" -> 5
        "X" -> 10
        "L" -> 50
        "C" -> 100
        "D" -> 500
        "M" -> 1000
        else -> -1
    }
}

class Student(val score: BehaviorSubject<Int>)

const val sentinel = -1