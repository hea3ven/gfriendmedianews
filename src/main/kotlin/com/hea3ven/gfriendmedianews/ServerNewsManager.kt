package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.domain.ServerConfig
import com.hea3ven.gfriendmedianews.domain.SourceConfig
import com.hea3ven.gfriendmedianews.news.post.NewsPost
import com.hea3ven.gfriendmedianews.news.source.InstagramNewsSource
import com.hea3ven.gfriendmedianews.news.source.TwitterNewsSource
import com.hea3ven.gfriendmedianews.news.source.YouTubeNewsSource
import com.hea3ven.gfriendmedianews.persistance.Persistence
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class ServerNewsManager(val serverConfig: ServerConfig) {

	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.MediaNews")

	fun fetchNews(persistence: Persistence, discord: DiscordAPI) {
		val server = discord.getServerById(serverConfig.serverId)
		if (serverConfig.targetChannel == null) {
			logger.debug("The channel is not configured for the server {}", server.name)
			return
		}
		val channel = server.getChannelById(serverConfig.targetChannel)
		if (channel == null) {
			logger.debug("The channel is not configured for the server {}", server.name)
			return
		}
		logger.trace("Updating the news for the server {}", server.name)
		persistence.beginTransaction().use { sess ->
			serverConfig.sourceConfigs
					.map {
						fetchNews(it, sess)
					}
					.reduce { result, news -> result.plus(news) }
					.forEach { it.post(channel);Thread.sleep(2000) }
		}
	}

	private fun fetchNews(it: SourceConfig, sess: PersistenceTransaction): List<NewsPost> {
		try {
			return getSource(it).fetchNews(it)
		} catch(e: Exception) {
			logger.error("Could not fetch news", e)
			return listOf()
		} finally {
			sess.sourceConfigDao.persist(it)
		}
	}

	fun setChannel(persistence: Persistence, channel: String) {
		serverConfig.targetChannel = channel
		persistence.beginTransaction().use { sess ->
			sess.serverConfigDao.persist(serverConfig)
		}
	}

	fun addSource(persistence: Persistence, srcType: String, srcData: String) {
		if (serverConfig.sourceConfigs.any { it.type == srcType && it.connectionData == srcData })
			throw IllegalArgumentException("The source already exists")
		val sourceConfig = SourceConfig(serverConfig, srcType, srcData)
		getSource(sourceConfig)
		serverConfig.sourceConfigs.add(sourceConfig)
		persistence.beginTransaction().use { sess ->
			sess.serverConfigDao.persist(serverConfig)
		}
	}

	fun showInfo(server: Server, message: Message) {
		val output = StringBuilder()
		output.append("News channel: " + getChannelDisplay(server) + "\n")
		output.append("Sources:" + "\n")
		serverConfig.sourceConfigs.forEach {
			output.append("    - " + it.connectionData + " (" + it.type + ")\n")
		}
		message.reply(output.toString())
	}

	fun getChannelDisplay(server: Server): String {
		if (serverConfig.targetChannel == null)
			return "not set"
		val channel = server.getChannelById(serverConfig.targetChannel)
		if (channel == null)
			return "invalid"
		else
			return "<#" + channel.id + ">"
	}

}

val twitterSource = TwitterNewsSource()
val instagramSource = InstagramNewsSource()
val youtubeSource = YouTubeNewsSource()
fun getSource(sourceConfig: SourceConfig) = when (sourceConfig.type) {
	"twitter" -> twitterSource
	"instagram" -> instagramSource
	"youtube" -> youtubeSource
	else -> throw IllegalArgumentException(sourceConfig.type + " is not a valid source type")
}
