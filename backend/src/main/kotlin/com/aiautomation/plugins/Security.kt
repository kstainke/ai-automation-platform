package com.aiautomation.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

object Security {
    private lateinit var secret: String
    private lateinit var issuer: String
    private var expiryMs: Long = 0

    fun init(secret: String, issuer: String, expiryMs: Long) {
        this.secret = secret
        this.issuer = issuer
        this.expiryMs = expiryMs
    }

    fun hashPassword(plaintext: String): String = BCrypt.hashpw(plaintext, BCrypt.gensalt(12))

    fun verifyPassword(plaintext: String, hash: String): Boolean = BCrypt.checkpw(plaintext, hash)

    fun generateToken(userId: String, email: String): String =
        JWT.create()
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expiryMs))
            .sign(Algorithm.HMAC256(secret))
}