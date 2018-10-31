package org.decembrist.server

import io.ktor.content.default
import io.ktor.content.files
import io.ktor.content.static
import io.ktor.content.staticRootFolder
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import java.io.File
import java.util.concurrent.TimeUnit

class Server(private val staticPath: String, private val port: Int) {

    private lateinit var server: NettyApplicationEngine

    fun start(): Server {
        server = embeddedServer(Netty, port, "127.0.0.1") {
            routing {
                static {
                    staticRootFolder = File(staticPath)
                    files("")
                    default("index.html")
                }
            }
        }.start()
        return this
    }

    fun stop() = server.stop(0, 0, TimeUnit.SECONDS)

}