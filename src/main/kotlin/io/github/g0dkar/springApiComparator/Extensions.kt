package io.github.g0dkar.springApiComparator

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Returns the [HttpServletRequest.getHeaderNames] list and values as a [Map].
 */
fun HttpServletRequest.headersMap(): Map<String, String> =
    if (this.headerNames != null) {
        hashMapOf<String, String>().also { headers ->
            for (headerName in this.headerNames) {
                if (headerName != null) {
                    val headerValue = this.getHeader(headerName)

                    if (headerValue != null) {
                        headers[headerName] = headerValue
                    }
                }
            }
        }
    } else {
        emptyMap()
    }

/**
 * Returns the [HttpServletResponse.getHeaderNames] list and values as a [Map].
 */
fun HttpServletResponse.headersMap(): Map<String, String> =
    if (this.headerNames != null) {
        hashMapOf<String, String>().also { headers ->
            for (headerName in this.headerNames) {
                if (headerName != null) {
                    val headerValue = this.getHeader(headerName)

                    if (headerValue != null) {
                        headers[headerName] = headerValue
                    }
                }
            }
        }
    } else {
        emptyMap()
    }
