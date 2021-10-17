package ru.tutu

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TextLabel(
    val text:String
) {

}

fun TextLabel.toJson():String =
    Json.encodeToString(this)

fun String.parseToTextLabel():TextLabel =
    Json.decodeFromString(TextLabel.serializer(), this)
