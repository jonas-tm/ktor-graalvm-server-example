package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import com.example.services.NewsService
import com.example.services.NewsServiceImpl
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    DatabaseFactory.connect()
    val newsService = NewsServiceImpl()

    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        setupServer(newsService)
    }.start(wait = true)
}

fun Application.setupServer(newsService: NewsService) {
    configureHTTP()
    configureCallID()
    configureErrorHandling()
    configureMonitoring()
    configureAdministration()
    configureRouting(newsService)
    configureSerialization()
    configureRequestLogging()
}
