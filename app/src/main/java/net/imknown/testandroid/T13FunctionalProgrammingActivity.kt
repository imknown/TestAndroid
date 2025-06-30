package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.ext.zLog

class T13FunctionalProgrammingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val figures = listOf(2, 5, 9)
        zLog(figures.myFold(10F) { a, b -> a * b })

        val strings = listOf("a", "ab", "abc")
        val oddLength = compose(::isOdd, ::length)
        zLog(strings.filter(oddLength))

        zLog(addUsingOneCommonFunction(1, 2, 3))
        zLog(addUsingAnonymousFunctions(1)(2)(4))
        zLog(addLambdaExpressions(1)(2)(5))
    }

    // region [myFold]
    private fun <B, A> Collection<B>.myFold(
        initial: A,
        combine: (A, B) -> A,
    ): A {
        var accumulator = initial
        for (element: B in this) {
            accumulator = combine(accumulator, element)
        }
        return accumulator
    }
    // endregion [myFold]

    // region [compose]
    private fun isOdd(x: Int) = (x % 2 != 0)

    private fun length(s: String) = s.length

    private fun <A, B, C> compose(
        f: (B) -> C,
        g: (A) -> B,
    ): (A) -> C = { x -> f(g(x)) }
    // endregion [compose]

    // region [curry]
    private fun addUsingOneCommonFunction(a: Int, b: Int, c: Int) = a + b + c

    private fun addUsingAnonymousFunctions(a: Int) = fun(b: Int) = fun(c: Int) = a + b + c

    private fun addLambdaExpressions(a: Int) = { b: Int -> { c: Int -> a + b + c } }
    // endregion [curry]
}