/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package example.imageviewer

import example.imageviewer.view.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.tutu.Id
import ru.tutu.Node
import ru.tutu.parseToNode
import ru.tutu.toJson
import kotlin.random.Random

@Serializable
data class FirstResponse(val sessionId: String, val state: Node)

@Serializable
data class ClientValue(val stringValue: String)

suspend fun getFirstState(userId:String, clientStorage: Map<String, ClientValue>): FirstResponse {
    delay(1000)
    val session = Random.nextInt().toString()
    val state = ServerState(userId, 0)
    mapSessionToServerState[session] = state
    return FirstResponse(session, renderServerState(state, clientStorage))
}

val KEY_INPUT1 = "input1"

fun renderServerState(state: ServerState, clientStorage: Map<String, ClientValue>): Node {
    val encodedClientStorage = Json.encodeToString(clientStorage)
    val decoded = Json.decodeFromString<Map<String, ClientValue>>(encodedClientStorage)
    return verticalContainer {
        label("counter ${state.counter}")
        input("hint", KEY_INPUT1)
        label("Hello ${decoded.get(KEY_INPUT1)?.stringValue}")
        button(id = Id("button.send"), text = "send")
        horizontalContainer {
            rectangle(50, 50, 0xff00ff00u)
            rectangle(50, 50, 0xffffff00u)
            image("https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/1.jpg", 100, 100)
        }
    }
}

data class ServerState(
    val userId: String,
    val counter: Int
)

/**
 * Внимание новые короновирусные ограничения
 * Сохраняйте меры предосторожности. КАРТИНКА
 * Пожалуйста введите номер сертификата вакцины (если имеется)
 * Проверить отменённый рейс можно в списке заказов LINK
 * Если возникнут вопросы - пишите в чат поддержки LINK
 * Частые вопросы в связи с коронавирусом (доп. текст)
 */
fun serverReducer(state: ServerState, clientStorage: Map<String, ClientValue>, intent: Intent): ServerState {
    return when (intent) {
        is Intent.ButtonPressed -> {
            state.copy(
                counter = state.counter + 1
            )
        }
    }
}

val mapSessionToServerState: MutableMap<String, ServerState> = mutableMapOf()//todo ConcurrentHashMap
suspend fun networkReducer(sessionId: String, clientStorage: Map<String, ClientValue>, intent: Intent): Node {
    val state: ServerState = mapSessionToServerState[sessionId]
        ?: return Node.Leaf.Label("Session not found. Please restart Application")

    val newState = serverReducer(state, clientStorage, intent)
    mapSessionToServerState[sessionId] = newState
    val node = renderServerState(newState, clientStorage)
    return node.toJson().parseToNode()
}
