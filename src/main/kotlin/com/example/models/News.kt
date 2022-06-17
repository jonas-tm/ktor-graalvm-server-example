package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class NewsEntry(
    val id: Long,
    val title: String,
    val text: String,
)