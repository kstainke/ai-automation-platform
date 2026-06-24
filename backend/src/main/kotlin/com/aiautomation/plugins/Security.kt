package com.aiautomation.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.ApplicationConfig
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val expiryMs: Long,
    val refreshExpiryMs: Long
)

object Security {
    lateinit var jwtConfig: JwtConfig

    fun init(config: ApplicationConfig) {
        jwtConfig = JwtConfig(
            config.property("jwt.secret").getString(),
            config.property("jwt.issuer").getString(),
            config.property("jwt.expiration").getString().toLong(),
            config.property("jwt.refreshExpiryMs").getString().toLong()
        )
    }

    fun hashPassword(plaintext: String): String = BCrypt.hashpw(plaintext, BCrypt.gensalt(12))

    fun verifyPassword(plaintext: String, hash: String): Boolean = BCrypt.checkpw(plaintext, hash)

    fun generateToken(userId: String, email: String): String =
        JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiryMs))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

    fun generateRefreshToken(userId: String): String =
        JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withClaim("userId", userId)
            .withClaim("type", "refresh")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.refreshExpiryMs))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

    fun verifyRefreshToken(token: String): String? = try {
        JWT.require(Algorithm.HMAC256(jwtConfig.secret))
            .withIssuer(jwtConfig.issuer)
            .withClaim("type", "refresh")
            .build()
            .verify(token)
            .getClaim("userId")
            .asString()
    } catch (e: Exception) {
        null
    }
}