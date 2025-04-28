package com.itza2k.kore.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.itza2k.kore.db.KoreDatabase
import java.io.File

object DatabaseFactory {
    fun createDatabase(): KoreDatabase {
        try {
            // Try to use user home directory first
            val databaseDir = File(System.getProperty("user.home"), ".kore")
            if (!databaseDir.exists()) {
                val created = databaseDir.mkdirs()
                if (!created) {
                    // If we can't create in user home, fall back to temp directory
                    return createDatabaseInTempDir()
                }
            }

            // Check if we can write to this directory
            if (!databaseDir.canWrite()) {
                return createDatabaseInTempDir()
            }

            // Create the database file
            val databaseFile = File(databaseDir, "kore.db")
            val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

            // Create the database schema if it doesn't exist
            if (!databaseFile.exists()) {
                KoreDatabase.Schema.create(driver)
            }

            return KoreDatabase(driver)
        } catch (e: Exception) {
            // If anything goes wrong, fall back to temp directory
            println("Error creating database in home directory: ${e.message}")
            return createDatabaseInTempDir()
        }
    }

    private fun createDatabaseInTempDir(): KoreDatabase {
        // Use temp directory as fallback
        val tempDir = File(System.getProperty("java.io.tmpdir"), "kore")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }

        val databaseFile = File(tempDir, "kore.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        // Create the schema if needed
        if (!databaseFile.exists()) {
            KoreDatabase.Schema.create(driver)
        }

        return KoreDatabase(driver)
    }
}
