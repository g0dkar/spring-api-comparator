package io.github.g0dkar.springApiComparator

import io.github.g0dkar.springApiComparator.annotation.CompareApi
import io.github.g0dkar.springApiComparator.service.ApiComparator
import org.apache.hc.client5.http.classic.HttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@RequestScope
class ApiComparatorInterceptor(
    val httpClient: HttpClient
) : HandlerInterceptor {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(ApiComparatorInterceptor::class.java)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod && handler.hasMethodAnnotation(CompareApi::class.java)) {
            val annotation = handler.getMethodAnnotation(CompareApi::class.java)
            ApiComparator(request, response, handler, httpClient)
                .start(annotation!!.value)
        } else {
            LOG.info("Unknown handler: {} (type is {})", handler, handler.javaClass)
        }

        super.afterCompletion(request, response, handler, ex)
    }
}
