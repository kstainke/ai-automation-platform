package com.aiautomation

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun Application.module() {
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        get("/health") {
            call.respondText("OK")
        }

        get("/api/auth/register") {
            call.respond(mapOf("status" to "TODO", "message" to "Registration endpoint"))
        }

        get("/api/auth/login") {
            call.respond(mapOf("status" to "TODO", "message" to "Login endpoint"))
        }

        get("/api/documents") {
            call.respond(mapOf("status" to "TODO", "message" to "Documents listing"))
        }

        get("/api/chat") {
            call.respond(mapOf("status" to "TODO", "message" to "Chat endpoint"))
        }

        get("/api/search") {
            call.respond(mapOf("status" to "TODO", "message" to "Search endpoint"))
        }
    }
}
