package com.aiautomation.entities

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.datetime.datetime
import java.util.UUID
import kotlin.time.Clock

object RefreshTokens : UUIDTable("refresh_tokens") {
    val userId = reference("user_id", Users)
    val tokenHash = varchar("token_hash", 255)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
}

class RefreshToken(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RefreshToken>(RefreshTokens)

    var user by User referencedOn RefreshTokens.userId
    var tokenHash by RefreshTokens.tokenHash
    var expiresAt by RefreshTokens.expiresAt
    val createdAt by RefreshTokens.createdAt
}
