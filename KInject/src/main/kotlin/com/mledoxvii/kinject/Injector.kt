package com.mledoxvii.kinject

import com.mledoxvii.kinject.injectable.Injectable
import com.mledoxvii.kinject.resolver.Resolver

interface Injector {

    fun <Params, Element: Any> register(injectable: Injectable<Params, Element>, builder: (Injector, Params) -> Element)

    fun <Params, Element: Any> registerSingleton(
        injectable: Injectable<Params, Element>,
        builder: (Injector, Params) -> Element
    )

    fun <Params, Element: Any> resolve(resolver: Resolver<Params, Element>): Element
}
