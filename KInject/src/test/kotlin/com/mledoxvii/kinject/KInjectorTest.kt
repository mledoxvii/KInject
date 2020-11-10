package com.mledoxvii.kinject

import com.mledoxvii.kinject.injectable.BaseInjectable
import com.mledoxvii.kinject.injectable.NoParamsInjectable
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.BeforeTest
import kotlin.test.Test

class KInjectorTest {

    private lateinit var sut: Injector

    @BeforeTest
    fun setup() {
        sut = KInjector()
    }

    @Test
    fun `should return different instances when using default registration`() {
        sut.register(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1("") }

        val instance1: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)
        val instance2: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)

        assertNotSame(instance1, instance2)
    }

    @Test
    fun `should return same instance when using singleton registration`() {
        sut.registerSingleton(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1("") }

        val instance1: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)
        val instance2: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)

        assertSame(instance1, instance2)
    }

    @Test
    fun `should resolve dependencies recursively`() {
        val expectedClass1Arg = "This is ${ExampleClass1::class}"
        val expectedClass2Arg = "This is ${ExampleClass2::class}"

        sut.register(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1(expectedClass1Arg) }
        sut.register(ExampleClass2Injectable()) { _, _ -> ExampleClass2(
                expectedClass2Arg,
                sut.resolve(ExampleClass1DefaultInjectable().resolver)
        ) }

        val class2: ExampleClass2 = sut.resolve(ExampleClass2Injectable().resolver)

        assertEquals(expectedClass1Arg, class2.class1.arg)
        assertEquals(expectedClass2Arg, class2.arg)
    }

    @Test
    fun `should resolve when using arguments`() {
        val expectedArg = "This is ${ExampleClass2::class}"
        val expectedClass1 = ExampleClass1("This is ${ExampleClass1::class}")

        sut.register(ExampleClass2ArgsInjectable()) { _, (arg, class1) -> ExampleClass2(arg, class1) }

        val class2: ExampleClass2 = sut.resolve(ExampleClass2ArgsInjectable().using(Pair(
                expectedArg,
                expectedClass1
        )))

        assertEquals(expectedArg, class2.arg)
        assertEquals(expectedClass1, class2.class1)
    }

    @Test
    fun `should keep last builder when registering twice`() {
        val expectedArg = "This is ${ExampleClass1::class}"

        sut.register(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1("") }
        sut.register(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1(expectedArg) }

        val class1: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)

        assertEquals(expectedArg, class1.arg)
    }

    @Test
    fun `should resolve correctly when registering different injectables for same type`() {
        val expectedDefaultArg = "Default"
        val expectedHelloArg = "Hello"

        sut.register(ExampleClass1DefaultInjectable()) { _, _ -> ExampleClass1(expectedDefaultArg) }
        sut.register(ExampleClass1HelloInjectable()) { _, _ -> ExampleClass1(expectedHelloArg) }

        val defaultExample: ExampleClass1 = sut.resolve(ExampleClass1DefaultInjectable().resolver)
        val helloExample: ExampleClass1 = sut.resolve(ExampleClass1HelloInjectable().resolver)

        assertEquals(expectedDefaultArg, defaultExample.arg)
        assertEquals(expectedHelloArg, helloExample.arg)
    }
}

//region Example Class 1

data class ExampleClass1(var arg: String)

class ExampleClass1DefaultInjectable: NoParamsInjectable<ExampleClass1>()
class ExampleClass1HelloInjectable: NoParamsInjectable<ExampleClass1>()

//endregion

//region Example Class 2

data class ExampleClass2(var arg: String, var class1: ExampleClass1)

class ExampleClass2Injectable: NoParamsInjectable<ExampleClass2>()
class ExampleClass2ArgsInjectable: BaseInjectable<Pair<String, ExampleClass1>, ExampleClass2>()

//endregion
