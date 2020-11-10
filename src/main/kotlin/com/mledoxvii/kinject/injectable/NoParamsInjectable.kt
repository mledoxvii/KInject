package com.mledoxvii.kinject.injectable

import com.mledoxvii.kinject.resolver.BaseResolver
import com.mledoxvii.kinject.resolver.Resolver

abstract class NoParamsInjectable<Result: Any>: BaseInjectable<Unit, Result>() {

    val resolver: Resolver<Unit, Result>
        get() = BaseResolver(this, Unit)
}
