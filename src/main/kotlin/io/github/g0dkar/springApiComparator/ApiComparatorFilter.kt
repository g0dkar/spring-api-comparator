package io.github.g0dkar.springApiComparator

import io.github.g0dkar.springApiComparator.annotation.CompareApi
import io.github.g0dkar.springApiComparator.internal.ApiComparator
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiComparatorFilter(
    apiComparatorHttpClient: CloseableHttpClient = HttpClients.createSystem(),
    private val log: Logger = LoggerFactory.getLogger(ApiComparatorFilter::class.java)
) : OncePerRequestFilter() {
    
    private val apiComparator = ApiComparator(apiComparatorHttpClient)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val wrappedResponse = wrapResponse(response)

            filterChain.doFilter(request, wrappedResponse)

            wrappedResponse.copyBodyToResponse()

            val handler = request.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE)

            if (handler != null) {
                if (handler is HandlerMethod && handler.hasMethodAnnotation(CompareApi::class.java)) {
                    val annotation = handler.getMethodAnnotation(CompareApi::class.java)!!
                    apiComparator.compare(annotation.value, request, wrappedResponse)
                } else {
                    log.info("Unknown handler: {} (type={})", handler, handler.javaClass)
                }
            }
        } catch (e: Exception) {
            log.error("Error on filterChain!", e)
            throw e
        }
    }

    private fun wrapResponse(response: HttpServletResponse) =
        if (response !is ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper(response)
        } else {
            response
        }
}
