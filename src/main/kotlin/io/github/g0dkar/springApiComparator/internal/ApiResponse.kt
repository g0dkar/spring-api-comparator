package io.github.g0dkar.springApiComparator.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.g0dkar.springApiComparator.headersMap
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

data class ApiResponse(
    val code: Int,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: ByteArrayInputStream,
) {
    companion object {
        private val TYPE_REF = object : TypeReference<Map<String, Any>>() {}

        fun from(response: ContentCachingResponseWrapper): ApiResponse =
            ApiResponse(
                code = response.status,
                headers = response.headersMap().mapValues { listOf(it.value) },
                body = ByteArrayInputStream(response.contentAsByteArray),
            )

        fun from(response: CloseableHttpResponse): ApiResponse =
            ApiResponse(
                code = response.code,
                headers = response.headers.groupBy({ it.name }, { it.value }),
                body = ByteArrayInputStream(EntityUtils.toByteArray(response.entity) ?: byteArrayOf()),
            )
    }

    fun jsonBody(objectMapper: ObjectMapper): Map<String, Any> =
        body.reset().let { objectMapper.readValue(body, TYPE_REF) }

    fun stringBody(charset: Charset = Charset.defaultCharset()): String =
        body.bufferedReader(charset).use { it.readText() }
}
