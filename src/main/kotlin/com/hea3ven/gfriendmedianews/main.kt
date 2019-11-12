package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.Javacord
import io.prometheus.client.exporter.HTTPServer
import io.prometheus.client.hotspot.DefaultExports
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.main")
fun main(args: Array<String>) {
    try {
        val prometheusServer = initPrometheus()
        Persistence().use { persistence ->
            val api = Javacord.getApi(Config.discordApiToken, true)
            val mediaNews = ChinguBot(persistence, api)
            mediaNews.start()
        }
        prometheusServer.stop()
    } catch (e: Throwable) {
        logger.error("Unhandled exception", e)
    } finally {
        System.exit(0)
    }
}

fun initPrometheus(): HTTPServer {
    DefaultExports.initialize()
    return HTTPServer(8080, true)
}

