package org.fintech.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: Int?,
    val title: String?,
    val slug: String?,
    val address: String?,
    val phone: String?,
    @SerialName("is_stub")val isStub: Boolean?,
    @SerialName("site_url")val siteUrl: String?,
    val coords: Coords?,
    val subway: String?,
    @SerialName("is_closed")val isClosed: Boolean?,
    val location: String?
)