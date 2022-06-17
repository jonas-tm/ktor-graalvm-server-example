package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.database.tables.NewsTable
import com.example.models.NewsEntry
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

fun ResultRow.toNewsEntry(): NewsEntry {
    return NewsEntry(
        id = this[NewsTable.id].value,
        title = this[NewsTable.title],
        text = this[NewsTable.text]
    )
}

interface NewsService {
    suspend fun getAllNewsEntries(): List<NewsEntry>
    suspend fun getNewsEntry(id: Long): NewsEntry?
    suspend fun addNewsEntry(newsEntry: NewsEntry): NewsEntry
}

class NewsServiceImpl : NewsService {

    override suspend fun getAllNewsEntries(): List<NewsEntry> = dbQuery {
        NewsTable.selectAll().map { it.toNewsEntry() }
    }

    override suspend fun getNewsEntry(id: Long): NewsEntry? = dbQuery {
        NewsTable.select {
            (NewsTable.id eq id)
        }.map { it.toNewsEntry()  }
            .singleOrNull()
    }

    override suspend fun addNewsEntry(newsEntry: NewsEntry): NewsEntry = dbQuery {
        val id = NewsTable.insertAndGetId{
            it[title] = newsEntry.title
            it[text] = newsEntry.text
        }
        newsEntry.copy(id = id.value)
    }

}