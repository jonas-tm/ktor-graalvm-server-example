package com.example

import io.ktor.server.engine.*
import com.example.plugins.*
import io.ktor.server.cio.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureHTTP()
        configureAdministration()
        configureRouting()
        configureSerialization()
        configureRequestLogging()
    }.start(wait = true)
}
