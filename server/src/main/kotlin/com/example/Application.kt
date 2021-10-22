package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

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
        static("/static") {
            resources("static")
        }
    }
}
