package org.fintech.dto

import kotlinx.serialization.Serializable

@Serializable
data class Coords (
    val lat: Double?,
    val lon: Double?
)