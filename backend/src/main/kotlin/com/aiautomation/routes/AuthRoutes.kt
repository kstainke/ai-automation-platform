package com.aiautomation.routes

import com.aiautomation.dao.UserDao
import com.aiautomation.plugins.Security
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable data class RegisterRequest(val email: String, val password: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class RegisterResponse(val userId: String, val email: String)
@Serializable data class LoginResponse(val token: String, val userId: String)
@Serializable data class ErrorResponse(val error: String)

fun Route.authRoutes() {
    post("/api/auth/register") {
        val body = call.receive<RegisterRequest>()

        if (body.password.length < 8) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("password must be at least 8 characters"))
            return@post
        }

        if (UserDao.findByEmail(body.email) != null) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse("email already registered"))
            return@post
        }

        val user = UserDao.create(body.email, Security.hashPassword(body.password))
        call.respond(HttpStatusCode.Created, RegisterResponse(user.id.toString(), user.email))
    }

    post("/api/auth/login") {
        val body = call.receive<LoginRequest>()
        val user = UserDao.findByEmail(body.email)

        if (user == null || !Security.verifyPassword(body.password, user.passwordHash)) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid credentials"))
            return@post
        }

        val token = Security.generateToken(user.id.toString(), user.email)
        call.respond(LoginResponse(token, user.id.toString()))
    }
}