/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package example.imageviewer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import example.imageviewer.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.launch
import ru.tutu.*
import ru.tutu.ReducerResult2

private sealed class RefreshViewState {
    object Loading : RefreshViewState()
    data class Loaded(val store: Store<Node, ClientIntent>) : RefreshViewState()
}

sealed class ClientIntent() {
    class SendToServer(val intent: Intent):ClientIntent()
    data class UpdateClientStorage(val key: String, val value: ClientValue) : ClientIntent()
}

val ktorClient:HttpClient = HttpClient(CIO)
val SERVER_URL = "http://localhost:8081"

suspend fun getFirstState(userId:String, clientStorage: Map<String, ClientValue>): FirstResponse {
    return ktorClient.post<String>("$SERVER_URL/${SERVER_PATH_FIRST_REQUEST}"){
        body = TextContent(FirstRequestBody(userId, clientStorage).toJson(), ContentType.Application.Json)
    }.parseToFirstResponse()
}

suspend fun networkReducer(sessionId: String, clientStorage: Map<String, ClientValue>, intent: Intent): Node =
    ktorClient.post<String>("$SERVER_URL/$SERVER_PATH_NETWORK_REDUCER"){
        body = TextContent(NetworkReducerRequestBody(sessionId, clientStorage, intent).toJson(), ContentType.Application.Json)
    }.parseToReducerResult().state

@Composable
fun RefreshView() {
    var globalState: RefreshViewState by remember {
        mutableStateOf(RefreshViewState.Loading)
    }
    var clientStorage by remember { mutableStateOf(emptyMap<String, ClientValue>()) }
    remember {
        APP_SCOPE.launch {
            val firstResponse = getFirstState("my UID", clientStorage)
            val store: Store<Node, ClientIntent> = createStore(firstResponse.reducerResult.state) { s: Node, a: ClientIntent ->
                when(a) {
                    is ClientIntent.UpdateClientStorage -> {
                        clientStorage = clientStorage.toMutableMap().also {
                            it[a.key] = a.value
                        }
                        //todo накапливать изменения и отправлять на сервер
                        s
                    }
                    is ClientIntent.SendToServer -> {
                        networkReducer(firstResponse.sessionId, clientStorage, a.intent)
                    }
                }
            }
            globalState = RefreshViewState.Loaded(store)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        globalState.let { s ->
            when (s) {
                is RefreshViewState.Loading -> {
                    CircularProgressIndicator(strokeWidth = 8.dp)
                }
                is RefreshViewState.Loaded -> {
                    val nodeState by s.store.stateFlow.collectAsState()
                    RenderNode(clientStorage, nodeState) {
                        s.store.send(it)
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkImage(imageUrl: String, width: Int, height: Int) {
    var mutableImage by remember { mutableStateOf<ImageBitmap?>(null) }
    remember {
        APP_SCOPE.launch {
            mutableImage = downloadImageBitmap(imageUrl)
        }
    }
    val image = mutableImage
    if(image != null) {
        Image(
            BitmapPainter(image),
            contentDescription = null,
            modifier = Modifier.size(width.dp, height.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = Modifier.size(width.dp, height.dp).background(color = Color.LightGray),
            contentAlignment = Alignment.Center) {
            Text("Loading image...")
        }
    }
}

@Composable
fun RenderNode(clientStorage:Map<String, ClientValue>, node: Node, sendIntent: (ClientIntent) -> Unit) {
    when (node) {
        is Node.Container.H -> {
            Row {
                for (child in node.children) {
                    RenderNode(clientStorage, child, sendIntent)
                }
            }
        }
        is Node.Container.V -> {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                for (child in node.children) {
                    RenderNode(clientStorage, child, sendIntent)
                }
            }
        }
        is Node.Leaf.Rectangle -> {
            Box(modifier = Modifier.size(node.width.dp, node.height.dp).background(color = Color(node.color.toInt())))
        }
        is Node.Leaf.Label -> {
            Text(text = node.text)
        }
        is Node.Leaf.Button -> {
            Button(onClick = {
                sendIntent(ClientIntent.SendToServer(Intent.ButtonPressed(node.id)))
            }) {
                Text(text = node.text)
            }
        }
        is Node.Leaf.Input -> {
            val text = clientStorage[node.storageKey]?.stringValue ?: ""
            TextField(text, onValueChange = {
                sendIntent(ClientIntent.UpdateClientStorage(node.storageKey, ClientValue(it)))
            })
        }
        is Node.Leaf.Image -> {
            NetworkImage(node.imgUrl, node.width, node.height)
        }
    }.also { }
}
