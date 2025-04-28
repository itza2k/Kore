package com.itza2k.kore.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.itza2k.kore.db.KoreDatabase
import java.io.File

object DatabaseFactory {
    fun createDatabase(): KoreDatabase {
        // Create the database directory if it doesn't exist
        val databaseDir = File(System.getProperty("user.home"), ".kore")
        if (!databaseDir.exists()) {
            databaseDir.mkdirs()
        }

        // Create the database file
        val databaseFile = File(databaseDir, "kore.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        // Create the database schema if it doesn't exist
        if (!databaseFile.exists()) {
            KoreDatabase.Schema.create(driver)
        }

        return KoreDatabase(driver)
    }
}