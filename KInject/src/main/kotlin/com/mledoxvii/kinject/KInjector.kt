package com.mledoxvii.kinject

import com.mledoxvii.kinject.injectable.Injectable
import com.mledoxvii.kinject.resolver.Resolver

open class KInjector: Injector {

    private val builders: MutableMap<String, Any> = mutableMapOf()
    private val singletonInjectables: MutableSet<String> = mutableSetOf()
    private val singletonInstances: MutableMap<String, Any> = mutableMapOf()

    override fun <Params, Element: Any> register(
        injectable: Injectable<Params, Element>,
        builder: (Injector, Params) -> Element
    ) {
        val key = buildKey(injectable)

        builders[key] = builder
    }

    override fun <Params, Element: Any> registerSingleton(
        injectable: Injectable<Params, Element>,
        builder: (Injector, Params) -> Element
    ) {
        val key = buildKey(injectable)

        builders[key] = builder
        singletonInjectables.add(key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Params, Element: Any> resolve(resolver: Resolver<Params, Element>): Element {
        val key = buildKey(resolver.injectable)
        val builder = builders[key] as? (Injector, Params) -> Element ?:
            throw IllegalStateException("No resolver registered for ${resolver.injectable}")

        val instance: Element = singletonInstances[key] as? Element ?: builder(this, resolver.parameters)

        if (singletonInjectables.contains(key)) singletonInstances[key] = instance

        return instance
    }

    private fun <Params, Result: Any> buildKey(injectable: Injectable<Params, Result>): String = "${injectable::class}"
}