package com.aiautomation.routes

import com.aiautomation.dao.RefreshTokenDao
import com.aiautomation.dao.UserDao
import com.aiautomation.plugins.Security
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@Serializable data class RegisterRequest(val email: String, val password: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class RefreshRequest(val refreshToken: String)
@Serializable data class RegisterResponse(val userId: String, val email: String)
@Serializable data class LoginResponse(val accessToken: String, val refreshToken: String, val userId: String)
@Serializable data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
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

        val accessToken = Security.generateToken(user.id.toString(), user.email)
        val refreshToken = Security.generateRefreshToken(user.id.toString())

        RefreshTokenDao.create(user, Security.hashPassword(refreshToken), refreshTokenExpiry())

        call.respond(LoginResponse(accessToken, refreshToken, user.id.toString()))
    }

    post("/api/auth/refresh") {
        val body = call.receive<RefreshRequest>()

        val userId = Security.verifyRefreshToken(body.refreshToken) ?: run {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid or expired refresh token"))
            return@post
        }

        val user = UserDao.findById(UUID.fromString(userId)) ?: run {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid or expired refresh token"))
            return@post
        }

        val tokenRecord = RefreshTokenDao.findValidForUser(user) { hash ->
            Security.verifyPassword(body.refreshToken, hash)
        } ?: run {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid or expired refresh token"))
            return@post
        }

        val newAccessToken = Security.generateToken(user.id.toString(), user.email)
        val newRefreshToken = Security.generateRefreshToken(user.id.toString())

        RefreshTokenDao.rotate(tokenRecord, user, Security.hashPassword(newRefreshToken), refreshTokenExpiry())

        call.respond(RefreshTokenResponse(newAccessToken, newRefreshToken))
    }
}

private fun refreshTokenExpiry() = Clock.System.now()
    .plus(Security.jwtConfig.refreshExpiryMs.milliseconds)
    .toLocalDateTime(TimeZone.currentSystemDefault())
