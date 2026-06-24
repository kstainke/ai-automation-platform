package com.aiautomation.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun initDatabase(config: ApplicationConfig) {
    val ds = HikariDataSource(HikariConfig().apply {
        jdbcUrl = config.property("database.url").getString()
        username = config.property("database.user").getString()
        password = config.property("database.password").getString()
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
        minimumIdle = 2
    })

    Database.connect(ds)

    Flyway.configure()
        .dataSource(ds)
        .locations("classpath:database/migrations")
        .load()
        .migrate()
}