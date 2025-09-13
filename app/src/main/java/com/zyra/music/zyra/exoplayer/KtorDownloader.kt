package com.zyra.music.zyra.exoplayer

import com.zyra.music.zyra.data.remote.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException

class KtorDownloader private constructor(
    private val client: HttpClient
) : Downloader() {

    companion object {
        @Volatile
        private var instance: KtorDownloader? = null

        fun getInstance(): KtorDownloader {
            return instance ?: synchronized(this) {
                instance ?: KtorDownloader(HttpClientFactory.create()).also { instance = it }
            }
        }
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response? {
        return runBlocking {
            doAsyncExecute(request)
        }
    }

    private suspend fun doAsyncExecute(request: Request): Response = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = when (request.httpMethod()) {
                "GET" -> client.get(request.url()) {
                    headers {
                        request.headers().forEach { (key, values) ->
                            values.forEach { value -> append(key, value) }
                        }
                    }
                }
                "POST" -> client.post(request.url()) {
                    headers {
                        request.headers().forEach { (key, values) ->
                            values.forEach { value -> append(key, value) }
                        }
                    }
                    request.dataToSend()?.let { setBody(it) }
                }
                "HEAD" -> client.head(request.url()) {
                    headers {
                        request.headers().forEach { (key, values) ->
                            values.forEach { value -> append(key, value) }
                        }
                    }
                }
                else -> throw UnsupportedOperationException("Unsupported Http Method: ${request.httpMethod()}")
            }

            Response(
                response.status.value,
                response.status.description,
                response.headers.entries().associate { it.key to it.value },
                response.bodyAsText(),
                request.url()
            )
        } catch (e: Exception) {
            throw IOException("Ktor request failed for URL: ${request.url()}", e)
        }


    }

//    @Throws(IOException::class, ReCaptchaException::class)
//    override fun get(url: String): Response {
//        val request = Request("GET", url)
//        return execute(request)
//    }
//
//    @Throws(IOException::class)
//    override fun head(url: String): Response {
//        val request = Request("HEAD", url)
//        return execute(request)
//    }
}



//    @Throws(IOException::class, ReCaptchaException::class)
//    override fun execute(request: Request): Response {
//        return runBlocking {
//            try {
//                val response: HttpResponse = when (request.httpMethod()) {
//                    "GET" -> client.get(request.url()) {
//                        headers {
//                            request.headers().forEach { (key, values) ->
//                                values.forEach { value -> append(key, value) }
//                            }
//                        }
//                    }
//                    "POST" -> client.post(request.url()) {
//                        headers {
//                            request.headers().forEach { (key, values) ->
//                                values.forEach { value -> append(key, value) }
//                            }
//                        }
//                        request.dataToSend()?.let { setBody(it) }
//                    }
//                    "HEAD" -> client.head(request.url()) {
//                        headers {
//                            request.headers().forEach { (key, values) ->
//                                values.forEach { value -> append(key, value) }
//                            }
//                        }
//                    }
//                    else -> throw UnsupportedOperationException("Unsupported Http Method: ${request.httpMethod()}")
//                }
//
//                Response(
//                    response.status.value,
//                    response.status.description,
//                    response.headers.entries().associate { it.key to it.value },
//                    response.bodyAsText(),
//                    request.url()
//                )
//            } catch (e: Exception) {
//                throw IOException("Ktor request failed for URL: ${request.url()}", e)
//            }
//        }
//    }