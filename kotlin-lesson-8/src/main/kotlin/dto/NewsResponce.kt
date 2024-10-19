package org.fintech.dto

import News
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponce(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<News>
)
