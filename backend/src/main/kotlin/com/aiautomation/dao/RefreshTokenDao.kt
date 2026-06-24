package com.aiautomation.dao

import com.aiautomation.entities.RefreshToken
import com.aiautomation.entities.RefreshTokens
import com.aiautomation.entities.User
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

object RefreshTokenDao {

    fun create(user: User, tokenHash: String, expiresAt: LocalDateTime): RefreshToken = transaction {
        RefreshToken.new {
            this.user = user
            this.tokenHash = tokenHash
            this.expiresAt = expiresAt
        }
    }

    fun findValidForUser(user: User, verifyHash: (String) -> Boolean): RefreshToken? = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val (expired, active) = RefreshToken.find { RefreshTokens.userId eq user.id }
            .toList()
            .partition { it.expiresAt <= now }

        expired.forEach { it.delete() }

        active.firstOrNull { verifyHash(it.tokenHash) }
    }

    fun rotate(old: RefreshToken, user: User, newHash: String, expiresAt: LocalDateTime): RefreshToken = transaction {
        old.delete()
        RefreshToken.new {
            this.user = user
            tokenHash = newHash
            this.expiresAt = expiresAt
        }
    }
}
