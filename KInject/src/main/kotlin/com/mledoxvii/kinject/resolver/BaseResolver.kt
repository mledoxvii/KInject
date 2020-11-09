package com.mledoxvii.kinject.resolver

import com.mledoxvii.kinject.injectable.Injectable

open class BaseResolver<Params, Result: Any>(
    override val injectable: Injectable<Params, Result>,
    override val parameters: Params
): Resolver<Params, Result>
