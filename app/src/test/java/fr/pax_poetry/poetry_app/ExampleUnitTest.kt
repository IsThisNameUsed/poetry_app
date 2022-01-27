package fr.pax_poetry.poetry_app

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.lang.Exception


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private var calculator: MainActivity? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        calculator = MainActivity()
    }

    @Test
    fun testMultiply() {
        assertEquals("Regular multiplication should work", calculator?.multiply(4, 5), 20)
    }

    @Test
    fun testMultiplyWithZero() {
        assertEquals("Multiple with zero should be zero", 0, calculator?.multiply(0, 5))
        assertEquals("Multiple with zero should be zero", 0, calculator?.multiply(5, 0))
    }
}