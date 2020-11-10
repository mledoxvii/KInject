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
