package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.Javacord
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.main")
fun main(args: Array<String>) {
	try {
		Persistence().use { persistence ->
			val api = Javacord.getApi(Config.discordApiToken, true)
			val mediaNews = ChinguBot(persistence, api)
			mediaNews.start()
		}
	} catch (e: Throwable) {
		logger.error("Unhandled exception", e)
	} finally {
		System.exit(0)
	}
}

