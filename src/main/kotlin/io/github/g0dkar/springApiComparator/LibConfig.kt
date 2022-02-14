package io.github.g0dkar.springApiComparator

import io.github.g0dkar.springApiComparator.config.Configs
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan("io.github.g0dkar.springApiComparator.components")
@ConfigurationPropertiesScan("io.github.g0dkar.springApiComparator.config")
class LibConfig {
    @Bean
    fun apiComparatorHttpClient(configs: Configs): CloseableHttpClient = HttpClients.createSystem()

    fun httpClientBuilder(): HttpClientBuilder =
        HttpClientBuilder.create()
            .apply {
                setUserAgent("userAgent")
            }
            .useSystemProperties()

    // fun httpClientBuilder(): HttpClientBuilder? {
    //     val clientBuilder: HttpClientBuilder = HttpClientBuilder.create()
    //     clientBuilder.setRequestExecutor(HttpRequestExecutorChain(chainableHttpRequestExecutors))
    //     clientBuilder.setConnectionManager(connectionManager)
    //     clientBuilder.setDefaultAuthSchemeRegistry(authSchemeRegistry)
    //     clientBuilder.setDefaultCredentialsProvider(credentialsProvider)
    //     clientBuilder.setDefaultRequestConfig(defaultRequestConfig)
    //     clientBuilder.setTargetAuthenticationStrategy(CookieProcessingTargetAuthenticationStrategy.INSTANCE)
    //     clientBuilder.setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
    //     clientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
    //     val userAgent: String = config.getGlobalConfiguration(ConfigurationConstants.USER_AGENT)
    //     clientBuilder.setUserAgent(userAgent)
    //     requestInterceptors.forEach(clientBuilder::addInterceptorLast)
    //     responseInterceptors.forEach(clientBuilder::addInterceptorLast)
    //     val cookieStore: CookieStore = cookieStoreProvider.getIfAvailable()
    //     if (config.isCookieManagementDisabled() || cookieStore == null) {
    //         clientBuilder.disableCookieManagement()
    //     } else {
    //         log.info("Using cookie store {}", cookieStore)
    //         clientBuilder.setDefaultCookieStore(cookieStore)
    //     }
    //     clientBuilder.setRoutePlanner(routePlanner)
    //     return clientBuilder
    // }
}
