/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package ru.tutu

import kotlinx.coroutines.delay
import kotlin.random.Random

suspend fun getFirstState(userId:String, clientStorage: Map<String, ClientValue>): FirstResponse {
    delay(300)
    val session = Random.nextInt().toString()
    val state = ServerState(userId, 0)
    mapSessionToServerState[session] = state
    return FirstResponse(session, renderServerState(state, clientStorage))
}

val KEY_INPUT1 = "input1"

fun renderServerState(state: ServerState, clientStorage: Map<String, ClientValue>): Node {
    val decoded = clientStorage.toJson().parseToClientStorage()
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

fun verticalContainer(lambda: NodeDsl.() -> Unit): Node = refreshViewDsl {
    verticalContainer {
        lambda()
    }
}.first()


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

@OptIn(ExperimentalStdlibApi::class)
private fun refreshViewDsl(lambda: NodeDsl.() -> Unit): List<Node> {
    return buildList<Node> {
        object : NodeDsl {
            override fun verticalContainer(lambda: NodeDsl.() -> Unit) {
                add(Node.Container.V(refreshViewDsl(lambda)))
            }

            override fun horizontalContainer(lambda: NodeDsl.() -> Unit) {
                add(Node.Container.H(refreshViewDsl(lambda)))
            }

            override fun button(id: Id, text: String) {
                add(Node.Leaf.Button(id, text))
            }

            override fun input(hint: String, storageKey: String) {
                add(Node.Leaf.Input(hint, storageKey))
            }

            override fun label(text: String) {
                add(Node.Leaf.Label(text))
            }

            override fun rectangle(width: Int, height: Int, color: UInt) {
                add(Node.Leaf.Rectangle(color = color, width = width, height = height))
            }

            override fun image(imgUrl: String, width: Int, height: Int) {
                add(Node.Leaf.Image(imgUrl, width, height))
            }
        }.lambda()
    }
}

interface NodeDsl {
    fun verticalContainer(lambda: NodeDsl.() -> Unit)
    fun horizontalContainer(lambda: NodeDsl.() -> Unit)
    fun button(id: Id, text: String)
    fun input(hint: String, storageKey: String)
    fun label(text: String)
    fun image(imgUrl: String, width: Int, height: Int)
    fun rectangle(width: Int, height: Int, color: UInt)
}

