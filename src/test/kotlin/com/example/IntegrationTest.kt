package com.example

import com.example.models.ErrorResp
import com.example.models.NewsEntry
import com.example.plugins.*
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
import kotlin.test.assertNotNull

val defaultEntry = NewsEntry(1, "A", "B")

class IntegrationTest {

    private fun testServer(newsService: NewsService, f: suspend (HttpClient) -> Unit) = testApplication {
        application {
            setupServer(newsService)
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
            assertNotNull(headers[REQUEST_ID_HEADER])
        }
    }

    @Test
    fun `GET all news with requestID`() = testServer(ErrorServiceMock()) {
        val id = "2238_asd1-85c"
        it.get("/api/v1/news") {
            header(REQUEST_ID_HEADER, id)
        }.apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
            assertEquals(id, headers[REQUEST_ID_HEADER])
        }
    }

    @Test
    fun `GET all news err`() = testServer(ErrorServiceMock()) {
        it.get("/api/v1/news").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
            assertNotNull(headers[REQUEST_ID_HEADER])
        }
    }

    @Test
    fun `GET specific news`() = testServer(NewsServiceMock()) {
        it.get("/api/v1/news/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(defaultEntry, body())
            assertNotNull(headers[REQUEST_ID_HEADER])
        }
    }

    @Test
    fun `GET specific news error`() = testServer(ErrorServiceMock()) {
        it.get("/api/v1/news/1").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
            assertEquals(DEFAULT_ERROR, body())
            assertNotNull(headers[REQUEST_ID_HEADER])
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
            assertNotNull(headers[REQUEST_ID_HEADER])
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
            assertNotNull(headers[REQUEST_ID_HEADER])
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