package io.github.g0dkar.springApiComparator

import io.github.g0dkar.springApiComparator.config.Configs
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan("io.github.g0dkar.springApiComparator.config")
class ApiComparatorConfig {
    @Bean
    fun apiComparatorInterceptor(apiComparatorHttpClient: CloseableHttpClient): ApiComparatorInterceptor =
        ApiComparatorInterceptor(apiComparatorHttpClient)

    @Bean
    fun apiComparatorHttpClient(
        configs: Configs,
        apiComparatorHttpClientBuilder: HttpClientBuilder
    ): CloseableHttpClient = apiComparatorHttpClientBuilder.build()

    @Bean
    fun apiComparatorHttpClientBuilder(): HttpClientBuilder =
        HttpClientBuilder.create()
            .apply {
                setUserAgent("userAgent")
            }
            .useSystemProperties()
}
