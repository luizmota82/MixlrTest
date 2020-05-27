package io.luizmota.mixlrtest

import java.util.*

/**
 *
 * Conversion classes using the JSON to Kotlin Android Studio
 * to make this faster
 */
data class StationResult(
    val `data`: Data
)

data class Data(
    val attributes: Attributes,
    val id: String,
    val links: LinksX,
    val type: String
)

data class Attributes(
    val name: String,
    val schedule: Schedule,
    val slug: String
)

data class LinksX(
    val self: String
)

data class Schedule(
    val `data`: List<DataX>
)

data class DataX(
    val attributes: AttributesX,
    val id: String,
    val links: Links,
    val type: String
)

data class AttributesX(
    val ends_at: Date,
    val host: String,
    val image_url: String,
    val slug: String,
    val starts_at: Date,
    val status: ShowStatus,
    val title: String
)

data class Links(
    val start_url: String,
    val stop_url: String
)
