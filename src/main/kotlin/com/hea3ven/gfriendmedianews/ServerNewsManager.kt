package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.mods.medianews.dao.NewsConfigDao
import com.hea3ven.gfriendmedianews.mods.medianews.model.NewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.mods.medianews.source.InstagramNewsSource
import com.hea3ven.gfriendmedianews.mods.medianews.source.YouTubeNewsSource
import com.hea3ven.gfriendmedianews.persistance.Persistence
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class ServerNewsManager(val serverId: String, val serverConfig: MutableList<NewsConfig>) {

    private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.ChinguBot")

    fun fetchNews(persistence: Persistence, server: Server) {
        // TODO: handle server no longer existing
        logger.trace("Updating the news for the server {}", server.name)
        persistence.beginTransaction().use { sess ->
            serverConfig.forEach { newsConfig ->
                val channel = server.getChannelById(newsConfig.channelId)
                if (channel == null) {
                    logger.debug("The channel is not configured for the news source {}", newsConfig)
                    return
                }
                fetchNews(newsConfig, sess).forEach { news ->
                    news.post(channel)
                    Thread.sleep(2000)
                }
            }
        }
    }

    private fun fetchNews(newsConfig: NewsConfig, sess: PersistenceTransaction): List<NewsPost> {
        try {
            val news = newsConfig.fetchNews()
            logger.debug("Found " + news.size + " new news for " + newsConfig.label)
            return news
        } catch (e: Exception) {
            logger.error("Could not fetch news", e)
            return listOf()
        } finally {
            val dao = sess.getDao(NewsConfigDao::class.java)
            dao.persist(newsConfig)
        }
    }

    fun addSource(persistence: Persistence, newsConfig: NewsConfig) {
        if (serverConfig.any(newsConfig::equalsData)){
            throw IllegalArgumentException("The source already exists")
        }
        serverConfig.add(newsConfig)
        persistence.beginTransaction().use { sess ->
            sess.getDao(NewsConfigDao::class.java).persist(newsConfig)
        }
    }

    fun showInfo(server: Server, message: Message) {
        val output = StringBuilder()
        output.append("Sources:" + "\n")
        serverConfig.forEach {
            val channel = getChannelDisplay(server, it.channelId)
            output.append("    - " + it.label + " (" + it.type + " |  " + channel + ")\n")
        }
        message.reply(output.toString())
    }

    fun getChannelDisplay(server: Server, channelId: String): String {
        val channel = server.getChannelById(channelId)
        if (channel == null) return "invalid"
        else return "<#" + channel.id + ">"
    }

}

val instagramSource = InstagramNewsSource()
val youtubeSource = YouTubeNewsSource()
//fun getSource(sourceConfig: SourceConfig) = when (sourceConfig.type) {
//	"twitter" -> twitterSource
//	"instagram" -> instagramSource
//	"youtube" -> youtubeSource
//	else -> throw IllegalArgumentException(sourceConfig.type + " is not a valid source type")
//}
