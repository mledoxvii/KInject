package com.mledoxvii.kinject.injectable

import com.mledoxvii.kinject.resolver.BaseResolver
import com.mledoxvii.kinject.resolver.Resolver

abstract class BaseInjectable<Params, Result: Any>: Injectable<Params, Result> {

    fun using(parameters: Params): Resolver<Params, Result> = BaseResolver(this, parameters)
}
