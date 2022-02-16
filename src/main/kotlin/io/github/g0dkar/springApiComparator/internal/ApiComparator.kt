package io.github.g0dkar.springApiComparator.internal

import com.google.common.base.Stopwatch
import io.github.g0dkar.springApiComparator.launch
import kotlinx.coroutines.Job
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.core5.http.message.BasicHeader
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class ApiComparator(
    private val httpClient: CloseableHttpClient,
    private val log: Logger = LoggerFactory.getLogger(ApiComparator::class.java)
) {
    fun compare(
        baseURI: String,
        originalApiRequest: ApiRequest,
        originalApiResponse: ApiResponse,
        stopwatch: Stopwatch = Stopwatch.createUnstarted(),
    ): Job {
        if (!stopwatch.isRunning) {
            stopwatch.start()
        }

        val job = launch(log) {
            val comparisonApiResponse = when (originalApiRequest.method) {
                "GET" -> doGet(originalApiRequest, baseURI)
                // "POST" -> doGet(baseURI)
                else -> throw UnsupportedOperationException("Unsupported HTTP method: ${originalApiRequest.method}")
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
        log.info("Comparison Request: {}", comparisonApiResponse)

        log.info("---")
        log.info("Comparison request took {}", stopwatch)
    }

    private fun doGet(request: ApiRequest, baseURI: String): ApiResponse {
        val headers = request.headers
            .map { BasicHeader(it.key, it.value) }
            .toTypedArray()

        val uri = URIBuilder(baseURI).apply {
            path = request.uri

            val queryParams = request.params.flatMap { paramsEntry ->
                paramsEntry.value.map { BasicNameValuePair(paramsEntry.key, it) }
            }

            setParameters(queryParams)
        }.build()

        val httpGet = HttpGet(uri).apply {
            setHeaders(*headers)
        }

        log.info("Executing: {} (uri={})", httpGet, uri)

        return httpClient.execute(httpGet).use {
            ApiResponse.from(it)
        }
    }
}
