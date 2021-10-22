package ru.tutu

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmInline

fun Node.toJson():String =
    Json.encodeToString(this)

fun String.parseToNode():Node =
    Json.decodeFromString(Node.serializer(), this)

@Serializable
sealed class Node() {
    @Serializable
    sealed class Leaf() : Node() {
        @Serializable
        data class Rectangle(val width: Int, val height: Int, val color: UInt) : Leaf()

        @Serializable
        data class Label(val text: String) : Leaf()

        @Serializable
        data class Button(val id: Id, val text: String) : Leaf()

        @Serializable
        data class Input(val hint: String, val storageKey: String) : Leaf()

        @Serializable
        data class Image(val imgUrl:String, val width: Int, val height: Int):Leaf()
    }

    @Serializable
    sealed class Container() : Node() {
        abstract val children: List<Node>

        @Serializable
        class H(override val children: List<Node>) : Container()

        @Serializable
        class V(override val children: List<Node>) : Container()
    }
}

@JvmInline
@Serializable
value class Id(val value: String)

@Serializable
data class FirstResponse(val sessionId: String, val state: Node)

@Serializable
data class ClientValue(val stringValue: String)

