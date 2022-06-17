package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val msg: String,
    val fields: Map<String, String>? = null
)