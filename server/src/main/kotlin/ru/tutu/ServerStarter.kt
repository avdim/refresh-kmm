package ru.tutu

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.tutu.*

fun main() {
    configureServer().start(wait = true)
}

fun startServer():AutoCloseable {
    val server = configureServer().start(wait = false)
    return AutoCloseable {
        server.stop(200, 400)
    }
}

private fun configureServer() =
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", watchPaths = listOf("classes", "resources")) {
        configureRouting()
    }

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World 5!")
        }
        post("/" + SERVER_PATH_FIRST_REQUEST) {
            val clientData = call.receiveText().parseToFirstRequestBody()
            val result: FirstResponse = getFirstState(clientData.userId, clientData.clientStorage)
            call.respondText(
                text = result.toJson(),
                contentType = ContentType.Application.Json
            )
        }
        post("/" + SERVER_PATH_NETWORK_REDUCER) {
            val clientData = call.receiveText().parseToNetworkReducerRequestBody()
            val result = networkReducer(clientData.sessionId, clientData.clientStorage, clientData.intent)
            call.respondText(
                text = result.toJson(),
                contentType = ContentType.Application.Json
            )
        }
        static("/static") {
            resources("static")
        }
    }
}
