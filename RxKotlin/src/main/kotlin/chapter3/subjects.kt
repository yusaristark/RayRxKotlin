import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject

fun main() {
    exampleOf("PublishSubject") {
        val publishSubject = PublishSubject.create<Int>()
        publishSubject.onNext(0)

        val subscriptionOne = publishSubject.subscribe { int ->
            println(int)
        }

        publishSubject.onNext(1)
        publishSubject.onNext(2)

        val subscriptionTwo = publishSubject.subscribe { int ->
            printWithLabel("2)", int)
        }

        publishSubject.onNext(3)

        subscriptionOne.dispose()

        publishSubject.onNext(4)

        publishSubject.onComplete()
        publishSubject.onNext(5)
        subscriptionTwo.dispose()

        val subscriptionThree = publishSubject.subscribeBy(
            onNext = {printWithLabel("3)", it)},
            onComplete = {printWithLabel("3)", "Complete")}
        )

        publishSubject.onNext(6)

        subscriptionThree.dispose()
    }

    // 1
    exampleOf("BehaviorSubject") {
        // 2
        val subscriptions = CompositeDisposable()
        // 3
        val behaviorSubject =
            BehaviorSubject.createDefault("Initial value")

        behaviorSubject.onNext("X")

        val subscriptionOne = behaviorSubject.subscribeBy(
            onNext = {printWithLabel("1)", it)},
            onError = {printWithLabel("1)", it.toString())}
        )

        // 1
        behaviorSubject.onError(RuntimeException("Error!"))
// 2
        subscriptions.add(behaviorSubject.subscribeBy(
            onNext = { printWithLabel("2)", it) },
            onError = { printWithLabel("2)", it.toString()) }
        ))
    }

    exampleOf("BehaviorSubject State") {

        val subscriptions = CompositeDisposable()
        val behaviorSubject = BehaviorSubject.createDefault(0)

        println(behaviorSubject.value)

        subscriptions.add(behaviorSubject.subscribeBy {
            printWithLabel("1)", it)
        })
        behaviorSubject.onNext(1)
        println(behaviorSubject.value)
        subscriptions.dispose()
    }

    exampleOf("ReplaySubject") {

        val subscriptions = CompositeDisposable()
        // 1
        val replaySubject = ReplaySubject.createWithSize<String>(2)
        // 2
        replaySubject.onNext("1")

        replaySubject.onNext("2")

        replaySubject.onNext("3")
        // 3
        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("1)", it) },
            onError = { printWithLabel("1)", it.toString())}
        ))

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("2)", it) },
            onError = { printWithLabel("2)", it.toString())}
        ))

        replaySubject.onNext("4")
        replaySubject.onError(RuntimeException("Error!"))

        subscriptions.add(replaySubject.subscribeBy(
            onNext = { printWithLabel("3)", it) },
            onError = { printWithLabel("3)", it.toString())}
        ))

        subscriptions.dispose()
    }

    exampleOf("AsyncSubject") {
        val subscriptions = CompositeDisposable()
        // 1
        val asyncSubject = AsyncSubject.create<Int>()
        // 2
        subscriptions.add(asyncSubject.subscribeBy(
            onNext = { printWithLabel("1)", it) },
            onComplete = { printWithLabel("1)", "Complete") }
        ))
        // 3
        asyncSubject.onNext(0)
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        // 4
        asyncSubject.onComplete()

        subscriptions.dispose()
    }

    exampleOf("RxRelay") {
        val subscriptions = CompositeDisposable()

        val publishRelay = PublishRelay.create<Int>()

        subscriptions.add(publishRelay.subscribeBy(
            onNext = { printWithLabel("1)", it) }
        ))

        publishRelay.accept(1)
        publishRelay.accept(2)
        publishRelay.accept(3)
    }

    exampleOf("blackjack") {
        val subscriptions = CompositeDisposable()

        val dealtHand = PublishSubject.create<List<Pair<String, Int>>>()

        fun deal(cardCount: Int) {
            val deck = cards
            var cardsRemaining = 52
            val hand = mutableListOf<Pair<String, Int>>()

            (0 until cardCount).forEach { _ ->
                val randomIndex = (0 until cardsRemaining).random()
                hand.add(deck[randomIndex])
                deck.removeAt(randomIndex)
                cardsRemaining -= 1
            }

            // Add code to update dealtHand here
            if (points(hand) > 21) {
                dealtHand.onError(HandError.Busted())
            } else {
                dealtHand.onNext(hand)
            }
        }

        // Add subscription to dealtHand here
        subscriptions.add(dealtHand.subscribeBy(
            onNext = { println("Hand: ${cardString(it)}\nPoints: ${points(it)}")},
            onError = { println(it) }
        ))

        deal(3)
    }
}