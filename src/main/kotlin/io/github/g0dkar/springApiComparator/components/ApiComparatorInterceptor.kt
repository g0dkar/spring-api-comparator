package io.github.g0dkar.springApiComparator.components

import io.github.g0dkar.springApiComparator.annotation.CompareApi
import io.github.g0dkar.springApiComparator.internal.ApiComparator
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ApiComparatorInterceptor(
    private val apiComparatorHttpClient: CloseableHttpClient,
    private val requestHolder: ThreadLocal<ContentCachingRequestWrapper> = ThreadLocal(),
    private val responseHolder: ThreadLocal<ContentCachingResponseWrapper> = ThreadLocal(),
) : HandlerInterceptor {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(ApiComparatorInterceptor::class.java)
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean =
        if (handler is HandlerMethod && handler.hasMethodAnnotation(CompareApi::class.java)) {
            super.preHandle(wrapRequest(request), wrapResponse(response), handler)
        } else {
            super.preHandle(request, response, handler)
        }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod) {
            if (handler.hasMethodAnnotation(CompareApi::class.java)) {
                val annotation = handler.getMethodAnnotation(CompareApi::class.java)!!
                val contentCachingRequestWrapper = requestHolder.get()
                val contentCachingResponseWrapper = responseHolder.get()

                ApiComparator(contentCachingRequestWrapper, contentCachingResponseWrapper, apiComparatorHttpClient)
                    .start(annotation.value)
            } else {
                LOG.debug("Method doesn't have the @CompareApi annotation: {}", handler)
            }
        } else {
            LOG.info("Unknown handler: {} (type is {})", handler, handler.javaClass)
        }

        super.afterCompletion(request, response, handler, ex)
    }

    private fun wrapRequest(request: HttpServletRequest): ContentCachingRequestWrapper =
        if (request is ContentCachingRequestWrapper) {
            request
        } else {
            ContentCachingRequestWrapper(request)
        }.also {
            requestHolder.set(it)
        }

    private fun wrapResponse(response: HttpServletResponse): ContentCachingResponseWrapper =
        if (response is ContentCachingResponseWrapper) {
            response
        } else {
            ContentCachingResponseWrapper(response)
        }.also {
            responseHolder.set(it)
        }
}
