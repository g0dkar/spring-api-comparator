package io.github.g0dkar.springApiComparator.internal

import io.github.g0dkar.springApiComparator.headersMap
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.ByteArrayInputStream

data class ApiRequest(
    val uri: String,
    val method: String,
    val params: Map<String, List<String>>,
    val headers: Map<String, String>,
    val body: ByteArrayInputStream
) {
    companion object {
        fun from(request: ContentCachingRequestWrapper): ApiRequest =
            ApiRequest(
                uri = request.requestURI,
                method = request.method.uppercase(),
                params = request.parameterMap.mapValues { it.value.asList() },
                headers = request.headersMap(),
                body = ByteArrayInputStream(request.contentAsByteArray)
            )
    }
}
