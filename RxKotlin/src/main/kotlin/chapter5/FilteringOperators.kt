import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

fun main() {
    exampleOf("ignoreElements") {
        val subscriptions = CompositeDisposable()
        val strikes = PublishSubject.create<String>()

        subscriptions.add(strikes.ignoreElements().subscribeBy { println("You're out") })

        strikes.onNext("X")
        strikes.onNext("X")
        strikes.onNext("X")
        strikes.onComplete()

        subscriptions.dispose()
    }

    exampleOf("elementAt") {
        val subscriptions = CompositeDisposable()
        val strikes = PublishSubject.create<String>()

        subscriptions.add(strikes.elementAt(2)
            .subscribeBy(
                onSuccess = { println("You're out") }
            ))

        strikes.onNext("X")
        strikes.onNext("X")
        strikes.onNext("X")

        subscriptions.dispose()
    }

    exampleOf("filter") {
        val subscriptions = CompositeDisposable()
        subscriptions.add(Observable.fromIterable(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
            .filter { number ->
                number > 5
            }.subscribeBy { println(it) })
        subscriptions.dispose()
    }

    exampleOf("skip") {
        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just("A", "B", "C", "D", "E", "F")
                // 2
                .skip(3)
                .subscribe {
                    println(it)
                })
    }

    exampleOf("skipWhile") {

        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just(2, 2, 3, 4)
                // 2
                .skipWhile { number ->
                    number % 2 == 0
                }.subscribe {
                    println(it)
                })

    }

    exampleOf("skipUntil") {

        val subscriptions = CompositeDisposable()
        // 1
        val subject = PublishSubject.create<String>()
        val trigger = PublishSubject.create<String>()

        subscriptions.add(
            // 2
            subject.skipUntil(trigger)
                .subscribe {
                    println(it)
                })

        subject.onNext("A")
        subject.onNext("B")
        trigger.onNext("X")
        subject.onNext("C")
    }

    exampleOf("take") {
        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just(1, 2, 3, 4, 5, 6)
                // 2
                .take(3)
                .subscribe {
                    println(it)
                })
    }

    exampleOf("takeWhile") {
        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.fromIterable(
                listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1))
                // 2
                .takeWhile { number ->
                    number < 5
                }.subscribe {
                    println(it)
                })
    }

    exampleOf("takeUntil") {
        val subscriptions = CompositeDisposable()
        // 1
        val subject = PublishSubject.create<String>()
        val trigger = PublishSubject.create<String>()

        subscriptions.add(
            // 2
            subject.takeUntil(trigger)
                .subscribe {
                    println(it)
                })
        // 3
        subject.onNext("1")
        subject.onNext("2")
    }

    exampleOf("distinctUntilChanged") {
        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just("Dog", "Cat", "Cat", "Dog")
                // 2
                .distinctUntilChanged()
                .subscribe {
                    println(it)
                })
    }

    exampleOf("distinctUntilChangedPredicate") {
        val subscriptions = CompositeDisposable()

        subscriptions.add(
            // 1
            Observable.just(
                    "ABC", "BCD", "CDE", "FGH", "IJK", "JKL", "LMN")
                // 2
                .distinctUntilChanged { first, second ->
                    // 3
                    second.any { it in first }
                }
                // 4
                .subscribe {
                    println(it)
                }
        )
    }
}