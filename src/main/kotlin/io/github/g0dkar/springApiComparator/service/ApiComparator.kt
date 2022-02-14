package io.github.g0dkar.springApiComparator.service

import io.github.g0dkar.springApiComparator.headersMap
import io.github.g0dkar.springApiComparator.launch
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.message.BasicHeader
import org.slf4j.LoggerFactory
import org.springframework.web.method.HandlerMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class ApiComparator(
    val request: HttpServletRequest,
    val response: HttpServletResponse,
    val handler: HandlerMethod,
    val httpClient: HttpClient
) {
    val originalResponseHeaders: Map<String, String> = response.headersMap()

    companion object {
        private val log = LoggerFactory.getLogger(ApiComparator::class.java)
    }

    fun start(baseURI: String) {
        launch(log) {
            when (request.method.uppercase()) {
                "GET" -> doGet(baseURI, request)
            }
        }
    }

    suspend fun doGet(baseURI: String, request: HttpServletRequest) {
        val httpGet = HttpGet(baseURI)
        val headers = headers(request)

        httpGet.setHeaders(*headers)

        /*
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
        System.out.println(response1.getCode() + " " + response1.getReasonPhrase());
        HttpEntity entity1 = response1.getEntity();
        // do something useful with the response body
        // and ensure it is fully consumed
        EntityUtils.consume(entity1);
    }

    HttpPost httpPost = new HttpPost("http://httpbin.org/post");
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("username", "vip"));
    nvps.add(new BasicNameValuePair("password", "secret"));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

    try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
        System.out.println(response2.getCode() + " " + response2.getReasonPhrase());
        HttpEntity entity2 = response2.getEntity();
        // do something useful with the response body
        // and ensure it is fully consumed
        EntityUtils.consume(entity2);
    }
         */
        // HttpGet httpGet = new HttpGet("http://httpbin.org/get");
    }

    private fun headers(request: HttpServletRequest): Array<Header> {
        val headersMap = request.headersMap()
        val headers = mutableListOf<Header>()

        headersMap.forEach { (key, value) ->
            headers.add(BasicHeader(key, value))
        }

        return headers.toTypedArray()
    }
}
