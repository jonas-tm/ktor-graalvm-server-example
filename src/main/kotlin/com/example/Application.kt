package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import com.example.services.NewsServiceImpl
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    DatabaseFactory.connect()
    val newsService = NewsServiceImpl()

    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureHTTP()
        configureAdministration()
        configureRouting(newsService)
        configureSerialization()
        configureRequestLogging()
    }.start(wait = true)
}
