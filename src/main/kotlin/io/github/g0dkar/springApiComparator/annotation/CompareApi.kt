package io.github.g0dkar.springApiComparator.annotation

/**
 * When added to a method that is also annotated with `@RequestMapping` and similar annotations, this will
 * asynchronously call a second API endpoint referenced by this annotation and compare the response generated by
 * the current request being executed and the other API.
 *
 * Consider this scenario:
 *
 * ```java
 * @CompareApi("https://httpbin.org/")
 * @GetMapping("/get")
 * public String example() {
 *     return "World!";
 * }
 * ```
 *
 * Invoking this endpoint will do also execute a `GET https://httpbin.org/get` and compare the responses from
 * this endpoint and what HTTP Bin returns.
 *
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CompareApi(
    /** Where the other API that should be compared to this one is hosted. */
    val value: String
)
