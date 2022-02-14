package io.github.g0dkar.springApiComparator

import org.apache.hc.core5.net.URIBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Returns the [HttpServletRequest.getHeaderNames] list and values as a [Map].
 */
fun HttpServletRequest.headersMap(): Map<String, String> {
    val headers = mutableMapOf<String, String>()

    for (headerName in this.headerNames) {
        headers[headerName] = this.getHeader(headerName)
    }

    return headers
}

/**
 * Returns the [HttpServletResponse.getHeaderNames] list and values as a [Map].
 */
fun HttpServletResponse.headersMap(): Map<String, String> {
    val headers = mutableMapOf<String, String>()

    for (headerName in this.headerNames) {
        headers[headerName] = this.getHeader(headerName)
    }

    return headers
}
