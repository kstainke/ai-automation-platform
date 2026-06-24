package com.aiautomation.dao

import com.aiautomation.entities.User
import com.aiautomation.entities.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

object UserDao {

    fun create(email: String, passwordHash: String): User = transaction {
        User.new {
            this.email = email
            this.passwordHash = passwordHash
        }
    }

    fun findByEmail(email: String): User? = transaction {
        User.find { Users.email eq email }.firstOrNull()
    }

    fun findById(id: UUID): User? = transaction {
        User.findById(id)
    }

    fun all(): List<User> = transaction {
        User.all().toList()
    }
}