package com.example

import com.example.models.ErrorResp
import com.example.models.NewsEntry
import com.example.plugins.DEFAULT_ERROR
import com.example.plugins.configureErrorHandling
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.services.NewsService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

val defaultEntry = NewsEntry(1, "A", "B")

class IntegrationTest {

    fun testServer(newsService: NewsService, f: suspend (HttpClient) -> Unit) = testApplication {
        application {
            configureSerialization()
            configureErrorHandling()
            configureRouting(newsService)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        f(client)
    }

    @Test
    fun `GET all news`() = testServer(NewsServiceMock()) {
        it.get("/api/v1/news").apply {
            assertEquals(HttpStatusCode.OK, status)
            val content = body<List<NewsEntry>>()
            assertEquals(1, content.size)
            assertEquals(defaultEntry, content.get(0))
        }
    }

    @Test
    fun `GET all news err`() = testServer(ErrorServiceMock()) {
        it.get("/api/v1/news").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
        }
    }

    @Test
    fun `GET specific news`() = testServer(NewsServiceMock()) {
        it.get("/api/v1/news/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(defaultEntry, body())
        }
    }

    @Test
    fun `GET specific news error`() = testServer(ErrorServiceMock()) {
        it.get("/api/v1/news/1").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
        }
    }

    @Test
    fun `POST specific news`() = testServer(NewsServiceMock()) {
        it.post("/api/v1/news") {
            contentType(ContentType.Application.Json)
            setBody(defaultEntry)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(defaultEntry, body())
        }
    }

    @Test
    fun `POST specific news error`() = testServer(ErrorServiceMock()) {
        it.post("/api/v1/news") {
            contentType(ContentType.Application.Json)
            setBody(defaultEntry)
        }.apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
        }
    }
}

private class NewsServiceMock : NewsService {
    override suspend fun getAllNewsEntries(): List<NewsEntry> {
        return listOf(defaultEntry)
    }

    override suspend fun getNewsEntry(id: Long): NewsEntry {
        return defaultEntry
    }

    override suspend fun addNewsEntry(newsEntry: NewsEntry): NewsEntry {
        return defaultEntry
    }
}

private class ErrorServiceMock : NewsService {
    override suspend fun getAllNewsEntries(): List<NewsEntry> {
        throw Exception("some error")
    }

    override suspend fun getNewsEntry(id: Long): NewsEntry {
        throw Exception("some error")
    }

    override suspend fun addNewsEntry(newsEntry: NewsEntry): NewsEntry {
        throw Exception("some error")
    }
}