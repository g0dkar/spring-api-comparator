package io.github.g0dkar.springApiComparator

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val comparatorScope = CoroutineScope(Dispatchers.IO)
private val comparatorScopeExceptionHandler = CoroutineExceptionHandler { _, exception ->
    doHandleException(exception = exception)
}

private fun doHandleException(log: Logger = LoggerFactory.getLogger("ApiComparator"), exception: Throwable) {
    log.error("{}: {}", exception.javaClass.name, exception.message, exception)
}

fun launch(log: Logger = LoggerFactory.getLogger("ApiComparator"), block: suspend CoroutineScope.() -> Unit) =
    comparatorScope.launch(comparatorScopeExceptionHandler) {
        try {
            block()
        } catch (t: Throwable) {
            doHandleException(log, t)
        }
    }
