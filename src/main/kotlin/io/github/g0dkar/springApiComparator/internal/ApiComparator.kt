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
import org.slf4j.LoggerFactory
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

internal class ApiComparator(
    private val request: ContentCachingRequestWrapper,
    response: ContentCachingResponseWrapper,
    private val httpClient: CloseableHttpClient,
    private val stopwatch: Stopwatch = Stopwatch.createUnstarted()
) {
    private val originalApiResponse: ApiResponse = ApiResponse.from(response)
    private lateinit var comparisonApiResponse: ApiResponse

    companion object {
        private val log = LoggerFactory.getLogger(ApiComparator::class.java)
    }

    fun start(baseURI: String): Job {
        stopwatch.start()

        val job = launch(log) {
            when (request.method.uppercase()) {
                "GET" -> doGet(baseURI)
                // "POST" -> doGet(baseURI)
                else -> log.error("Unsupported HTTP method: {}", request.method)
            }
        }

        job.invokeOnCompletion {
            stopwatch.stop()

            if (it == null && this::comparisonApiResponse.isInitialized) {
                log.info("This Service Response: {}", originalApiResponse)
                log.info("   >>> String: {}", originalApiResponse.stringBody())
                log.info("Comparison Request: {}", comparisonApiResponse)
                log.info("   >>> String: {}", comparisonApiResponse.stringBody())
            }
        }

        return job
    }

    private fun doGet(baseURI: String) {
        val headers = request.headersMap()
            .map { BasicHeader(it.key, it.value) }
            .toTypedArray()

        val uri = URIBuilder(baseURI).apply {
            path = request.requestURI

            request.parameterMap.forEach { (key, value) ->
                value.forEach {
                    queryParams.add(BasicNameValuePair(key, it))
                }
            }
        }.build()

        val httpGet = HttpGet(uri).apply {
            setHeaders(*headers)
        }

        httpClient.execute(httpGet).use {
            comparisonApiResponse = ApiResponse.from(it)
        }
    }
}
