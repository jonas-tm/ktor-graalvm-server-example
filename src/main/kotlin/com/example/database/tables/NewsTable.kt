package com.example.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object NewsTable : LongIdTable("news") {
    val title: Column<String> = varchar("title", 255)
    val text: Column<String> = varchar("text", 255)
}