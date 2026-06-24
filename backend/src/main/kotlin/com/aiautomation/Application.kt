package com.aiautomation

import com.aiautomation.database.initDatabase
import com.aiautomation.plugins.Security
import com.aiautomation.routes.authRoutes
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
    val config = environment.config

    Security.init(
        secret = config.property("jwt.secret").getString(),
        issuer = config.property("jwt.issuer").getString(),
        expiryMs = config.property("jwt.expiration").getString().toLong()
    )

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(CallLogging) {
        level = Level.INFO
    }

    initDatabase(config)

    routing {
        get("/health") {
            call.respondText("OK")
        }
        authRoutes()
    }
}