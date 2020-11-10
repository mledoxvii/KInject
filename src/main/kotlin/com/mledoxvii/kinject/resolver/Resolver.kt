package com.mledoxvii.kinject.resolver

import com.mledoxvii.kinject.injectable.Injectable

interface Resolver<Params, Element: Any> {
    val injectable: Injectable<Params, Element>
    val parameters: Params
}
