package com.example.database

import com.example.database.tables.NewsTable
import com.example.services.toNewsEntry
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {

    private val log = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun connect() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        transaction {

            SchemaUtils.create(NewsTable)

            NewsTable.insert {
                it[title] = "Test 1"
                it[text] = "This is a first sample news."
            }
            NewsTable.insert {
                it[title] = "Test 2"
                it[text] = "This is a second sample news."
            }

            val entries = NewsTable.selectAll().map { it.toNewsEntry() }
            log.info("Created sample entries {}", entries)
        }
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}