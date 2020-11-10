# KInject
Simple dependency injection framework for Kotlin

## Basic Usage

For a simple use-case where we have a class with a dependency like the following.

```kotlin
interface ApiClient {
    fun requestData(): String
}

class SomeController(private val apiClient: ApiClient) {

    fun onSomeEventTriggered() {
        print(apiClient.requestData())
    }
}
```

We may want to use a stubbed version of the client for our _Debug_ variant.

```kotlin
class ReleaseClient: ApiClient {

    override fun requestData(): String {
        // Perform api call
    }
}

class StubbedClient: ApiClient {

    override fun requestData(): String {
        return "Stubbed data"
    }
}
```

In order to inject the proper implementation we need to declare an _injectable_ for the `ApiClient` interface.

```kotlin
class ApiClientInjectable: NoParamsInjectable<ApiClient>()
```

Next, register the _injectable_ to the `Injector` instance providing the initialization logic.

```kotlin
val injector: Injector = KInjector()

injector.register(ApiClientInjectable()) { _, _ ->
    if (isDebugVariant) {
        StubbedClient()
    } else {
        ReleaseClient()
    }
}
```

Finally, you can inject the proper implementation using the injector.

```kotlin
val controller: SomeController = SomeController(injector.resolve(ApiClientInjectable().resolver))

controller.onSomeEventTriggered()
```

## Runtime Arguments

The injected elements may require arguments that cannot be resolved by the injector, like cases where the argument is calculated at runtime. For these cases you have to specify the arguments type in the _injectable_ using the `BaseInjectable<Params, Element>` class.

```kotlin
class MyClass(val arg1: String, val arg2: Int)

class MyClassInjectable: BaseInjectable<Pair<String, Int>, MyClass>()
```

The parameters are recieved in the _builder_ closure when registering the _injectable_.

```kotlin
injector.register(MyClassInjectable()) { _, (arg1, arg2) ->
    MyClass(arg1, arg2)
}
```

Then you have to supply the arguments when resolving the element.

```kotlin
val myClass: MyClass = injector.resolve(MyClassInjectable().using(Pair("Arg1", 2)))
```

## Registrators

The `Registrator` interface is provided to avoid a massive function where all elements are registered to the injector. With it you can separate elements registration in multiple modules like the following.

```kotlin
class HomeScreenRegistrator: Registrator {

    override fun registerOn(injector: Injector) {
        registerView(injector)
        registerController(injector)
        registerRouter(injector)
    }

    private fun registerView(injector: Injector) {
        injector.register(HomeViewInjectable()) { ... }
    }

    private fun registerController(injector: Injector) {
        injector.register(HomeControllerInjectable()) { ... }
    }

    private fun registerRouter(injector: Injector) {
        injector.register(HomeRouterInjectable()) { ... }
    }
}
```

Then you can initialize the app's injector passing in the list of registrators.

```kotlin
val injector: Injector = KInjector(listOf(
    HomeScreenRegistrator(),
    ...
))
```
