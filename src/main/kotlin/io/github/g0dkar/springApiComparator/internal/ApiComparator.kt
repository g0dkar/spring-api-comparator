package io.github.g0dkar.springApiComparator.internal

import com.google.common.base.Stopwatch
import io.github.g0dkar.springApiComparator.ApiResponse
import io.github.g0dkar.springApiComparator.headersMap
import io.github.g0dkar.springApiComparator.launch
import kotlinx.coroutines.Job
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.message.BasicHeader
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest

internal class ApiComparator(
    private val httpClient: CloseableHttpClient,
    private val log: Logger = LoggerFactory.getLogger(ApiComparator::class.java)
) {
    fun compare(
        baseURI: String,
        request: HttpServletRequest,
        response: ContentCachingResponseWrapper,
        stopwatch: Stopwatch = Stopwatch.createUnstarted(),
    ): Job {
        val originalApiResponse = ApiResponse.from(response)

        if (!stopwatch.isRunning) {
            stopwatch.start()
        }

        val job = launch(log) {
            val comparisonApiResponse = when (request.method.uppercase()) {
                "GET" -> doGet(request, baseURI)
                // "POST" -> doGet(baseURI)
                else -> throw UnsupportedOperationException("Unsupported HTTP method: ${request.method}")
            }

            if (stopwatch.isRunning) {
                stopwatch.stop()
            }

            finishComparison(originalApiResponse, comparisonApiResponse, stopwatch)
        }

        return job
    }

    private fun finishComparison(
        originalApiResponse: ApiResponse,
        comparisonApiResponse: ApiResponse,
        stopwatch: Stopwatch
    ) {
        log.info("This Service Response: {}", originalApiResponse)
        log.info("   >>> String: {}", originalApiResponse.stringBody())
        log.info("Comparison Request: {}", comparisonApiResponse)
        log.info("   >>> String: {}", comparisonApiResponse.stringBody())

        log.info("---")
        log.info("Comparison request took {}", stopwatch)
    }

    private fun doGet(request: HttpServletRequest, baseURI: String): ApiResponse {
        val headers = request.headersMap()
            .map { BasicHeader(it.key, it.value) }
            .toTypedArray()

        val uri = URIBuilder(baseURI).apply {
            path = request.requestURI

            for (param in request.parameterNames) {
                log.info("[req param] {}={}", param, request.getParameter(param))
            }

            request.parameterMap.forEach { (key, value) ->
                value.forEach {
                    log.info("[req param 2] {}={}", key, it)
                    queryParams.add(BasicNameValuePair(key, it))
                }
            }
        }.build()

        val httpGet = HttpGet(uri).apply {
            setHeaders(*headers)
        }

        log.info("Executing: {}", httpGet)
        log.info("  >>> Built from: {} (uri={})", request, request.requestURI)

        return httpClient.execute(httpGet).use {
            ApiResponse.from(it)
        }
    }
}
