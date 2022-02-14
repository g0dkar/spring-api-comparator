package io.github.g0dkar.springApiComparator.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConstructorBinding
@ConfigurationProperties("api-comparator")
data class Configs(
    /** UserAgent header value to be sent with requests sent by the API Comparator Library. */
    val userAgent: String
)
