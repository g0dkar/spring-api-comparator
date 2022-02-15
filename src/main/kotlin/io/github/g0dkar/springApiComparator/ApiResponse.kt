package io.github.g0dkar.springApiComparator

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class ApiResponse(
    val code: Int,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: ByteArray,
) {
    companion object {
        private val TYPE_REF = object : TypeReference<Map<String, Any>>() {}

        fun from(response: ContentCachingResponseWrapper): ApiResponse =
            ApiResponse(
                code = response.status,
                headers = response.headersMap().mapValues { listOf(it.value) },
                body = response.contentAsByteArray,
            )

        fun from(response: CloseableHttpResponse): ApiResponse =
            ApiResponse(
                code = response.code,
                headers = response.headers.groupBy({ it.name }, { it.value }),
                body = EntityUtils.toByteArray(response.entity) ?: byteArrayOf(),
            )
    }

    fun jsonBody(objectMapper: ObjectMapper): Map<String, Any> =
        objectMapper.readValue(body, TYPE_REF)

    fun stringBody(charset: Charset = Charset.defaultCharset()): String = String(body, charset)
}
