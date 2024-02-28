package com.shivamgupta

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(
    val session: DefaultWebSocketSession
) {

    val name = "user${lastId.getAndIncrement()}"

    companion object {
        val lastId = AtomicInteger(0)
    }

}