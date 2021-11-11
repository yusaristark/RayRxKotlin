import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

fun main(args: Array<String>) {
    exampleOf("toList") {

        val subscriptions = CompositeDisposable()
        // 1
        val items = Observable.just("A", "B", "C")

        subscriptions.add(
            items
                // 2
                .toList()
                .subscribeBy {
                    println(it)
                }
        )
    }

    exampleOf("map") {

        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just("M", "C", "V", "I")
                // 2
                .map {
                    // 3
                    it.romanNumeralIntValue()
                }
                // 4
                .subscribeBy {
                    println(it)
                })
    }

    exampleOf("flatMap") {
        val subscriptions = CompositeDisposable()

        val ryan = Student(BehaviorSubject.createDefault(80))
        val charlotte = Student(BehaviorSubject.createDefault(90))
        val student = PublishSubject.create<Student>()

        student.flatMap { it.score }
            .subscribe { println(it) }
            .addTo(subscriptions)

        student.onNext(ryan)
        ryan.score.onNext(85)
        student.onNext(charlotte)
        ryan.score.onNext(95)
        charlotte.score.onNext(100)
    }

    exampleOf("switchMap") {
        val ryan = Student(BehaviorSubject.createDefault(80))
        val charlotte = Student(BehaviorSubject.createDefault(90))
        val student = PublishSubject.create<Student>()

        student.switchMap { it.score }
            .subscribe { println(it) }

        student.onNext(ryan)
        ryan.score.onNext(85)
        student.onNext(charlotte)
        ryan.score.onNext(95)
        charlotte.score.onNext(100)
    }

    exampleOf("materialize/dematerealize") {
        val subscriptions = CompositeDisposable()

        val ryan = Student(BehaviorSubject.createDefault(80))
        val charlotte = Student(BehaviorSubject.createDefault(90))
        val student = BehaviorSubject.create<Student>()

        val studentScore = student.switchMap { it.score.materialize() }

        subscriptions.add(studentScore
            .filter {
                if (it.error != null) {
                    println(it.error)
                    false
                } else {
                    true
                }
            }
            .dematerialize { it }
            .subscribe {
                println(it)
            })

        student.onNext(ryan)
        ryan.score.onNext(85)
        ryan.score.onError(RuntimeException("Error!"))
        ryan.score.onNext(90)
        student.onNext(charlotte)
    }

    exampleOf("Challenge 1") {

        val subscriptions = CompositeDisposable()

        val contacts = mapOf(
            "603-555-1212" to "Florent",
            "212-555-1212" to "Junior",
            "408-555-1212" to "Marin",
            "617-555-1212" to "Scott")

        val convert: (String) -> Int = { value ->
            val number = try {
                value.toInt()
            } catch (e: NumberFormatException) {
                val keyMap = mapOf(
                    "abc" to 2, "def" to 3, "ghi" to 4, "jkl" to 5,
                    "mno" to 6, "pqrs" to 7, "tuv" to 8, "wxyz" to 9)

                keyMap.filter { it.key.contains(value.toLowerCase()) }.map { it.value }.first()
            }

            if (number < 10) {
                number
            } else {
                sentinel // RxJava 3 does not allow null in stream, so return sentinel value
            }
        }

        val format: (List<Int>) -> String = { inputs ->
            val phone = inputs.map { it.toString() }.toMutableList()
            phone.add(3, "-")
            phone.add(7, "-")
            phone.joinToString("")
        }

        val dial: (String) -> String = { phone ->
            val contact = contacts[phone]
            if (contact != null) {
                "Dialing $contact ($phone)..."
            } else {
                "Contact not found"
            }
        }

        val input = BehaviorSubject.createDefault<String>("$sentinel")

        // Add your code here
        input.map(convert)
            .filter { it != sentinel }
            .skipWhile { it == 0 }
            .take(10)
            .toList()
            .subscribeBy(onSuccess = {
                val number = format(it)
                println(dial(number))
            }, onError = {
                println(it)
            })

        input.onNext("617")
        input.onNext("0")
        input.onNext("408")

        input.onNext("6")
        input.onNext("212")
        input.onNext("0")
        input.onNext("3")

        "JKL1A1B".forEach {
            input.onNext(it.toString()) // Need toString() or else Char conversion is done
        }

        input.onNext("9")
    }
}
