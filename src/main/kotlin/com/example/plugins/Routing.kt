package com.example.plugins

import com.example.models.NewsEntry
import com.example.services.NewsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


const val NEWS_ID = "newsId"
fun Application.configureRouting(newsService: NewsService) {
    routing {

        get {
            call.respondText("Hello World!")
        }

        route("/api/v1") {

            route("/news") {

                get {
                    call.respond(newsService.getAllNewsEntries())
                }

                get("/{$NEWS_ID}") {
                    val id = call.parameters[NEWS_ID]?.toLongOrNull()
                    id?.let {
                        newsService.getNewsEntry(it)?.let {
                            call.respond(it)
                        } ?: call.respond(HttpStatusCode.NotFound, Error("No news entry with id $it."))

                    }?: call.respond(HttpStatusCode.NotFound, Error("Missing or invalid news id in path."))
                }

                post("") {
                    val newsEntry = call.receiveNullable<NewsEntry>()

                    newsEntry?.let {
                        call.respond(newsService.addNewsEntry(newsEntry))
                    } ?: call.respond(HttpStatusCode.BadRequest, Error("Invalid body"))

                }
            }
        }
    }
}
