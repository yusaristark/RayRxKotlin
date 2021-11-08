import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.roundToInt

fun main() {
    exampleOf("just") {
        val observable: Observable<Int> = Observable.just(1, 2, 3)
    }

    exampleOf("fromIterable") {
        val observable: Observable<Int> = Observable.fromIterable(listOf(1, 2, 3))
    }

    exampleOf("subscribe") {
        val observable = Observable.just(1, 2, 3)
        observable.subscribe {
            println(it)
        }
    }

    exampleOf("empty") {
        val observable = Observable.empty<Unit>()
        observable.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("Completed") }
        )
    }

    exampleOf("never") {
        val observable = Observable.never<Any>()

        val disposables = CompositeDisposable()

        disposables.add(observable.doOnSubscribe { println("Subscribed") }.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("Completed") }
        ))

    }

    exampleOf("range") {
        val observable = Observable.range(1, 10)
        observable.subscribe {
            val n = it.toDouble()
            val fibonacci = ((Math.pow(1.61803, n) - Math.pow(0.61803, n)) / 2.23606).roundToInt()
            println(fibonacci)
        }
    }

    exampleOf("dispose") {
        // 1
        val mostPopular: Observable<String> =
            Observable.just("A", "B", "C")
        // 2
        val subscription = mostPopular.subscribe {
            // 3
            println(it)
        }
        subscription.dispose()
    }

    exampleOf("CompsiteDisposable") {
        val subscriptions = CompositeDisposable()
        val disposable = Observable.just("A", "B", "C").subscribe {
            println(it)
        }
        subscriptions.add(disposable)
        subscriptions.dispose()
    }

    exampleOf("create") {
        val disposables = CompositeDisposable()

        Observable.create<String> { emitter ->
            emitter.onNext("1")

            emitter.onComplete()

            emitter.onNext("?")
        }.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("Completed") },
            onError = { println(it) }
        )
    }

    exampleOf("defer") {

        val disposables = CompositeDisposable()
        // 1
        var flip = false
        // 2
        val factory: Observable<Int> = Observable.defer {
            // 3
            flip = !flip
            // 4
            if (flip) {
                Observable.just(1, 2, 3)
            } else {
                Observable.just(4, 5, 6)
            }
        }

        for (i in 0..3) {
            disposables.add(
                factory.subscribe {
                    println(it)
                }
            )
        }

        disposables.dispose()
    }

    exampleOf("Single") {
        // 1
        val subscriptions = CompositeDisposable()
        // 2
        fun loadText(filename: String): Single<String> {
            // 3
            return Single.create create@{ emitter ->
                val file = File(filename)
                if (!file.exists()) {
                    emitter.onError(FileNotFoundException("Can't find $filename"))
                    return@create
                }
                val contents = file.readText(Charsets.UTF_8)
                emitter.onSuccess(contents)
            }
        }

        val observer = loadText("Copyright.txt").subscribeBy(
            onSuccess = { println(it) },
            onError = { println("Error, $it") }
        )

        subscriptions.add(observer)
    }
}