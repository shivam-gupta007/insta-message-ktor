package com.shivamgupta.plugins

import com.shivamgupta.Connection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.Collections

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") { // websocketSession
            println("Adding user")

            val thisConnection = Connection(session = this)
            connections += thisConnection

            try {
                send("You are connected! There are ${connections.count()} users")
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val textWithUserName = "[${thisConnection.name}] $text"
                        connections.forEach {
                            it.session.send(textWithUserName)
                        }
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                }
            } catch (e: Exception){
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}
