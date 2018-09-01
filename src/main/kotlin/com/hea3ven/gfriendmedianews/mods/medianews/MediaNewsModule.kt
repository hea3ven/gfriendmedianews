package com.hea3ven.gfriendmedianews.mods.medianews

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.ServerNewsManager
import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.medianews.dao.NewsConfigDao
import com.hea3ven.gfriendmedianews.mods.medianews.dao.ServerConfigDaoFactory
import com.hea3ven.gfriendmedianews.mods.medianews.model.TwitterNewsConfig
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import com.hea3ven.gfriendmedianews.util.getChannelId
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class MediaNewsModule(val bot: ChinguBot) : Module {

    init {
        bot.persistence.registerDaoFactory(NewsConfigDao::class.java, ServerConfigDaoFactory())
    }

    private val logger = LoggerFactory.getLogger(MediaNewsModule::class.java)

    override val commands = listOf(ActionCommand("addsource",
                                                 " **\$addsource [channel] [type] [username]**: Adds a news source.\n" + "    **type** can be one of:\n" + "        \\* twitter\n" + "        \\* instagram\n" + "        \\* youtube\n" + "    examples:\n" + "        \\* \$addsource #official_media twitter @GFRDOfficial\n" + "        \\* \$addsource #official_media instagram gfriendofficial\n" + "        \\* \$addsource #official_media youtube gfrdofficial",
                                                 this::onAddSrc, true), ActionCommand("mediainfo",
                                                                                      " **\$mediainfo**: Show the configuration and information of the media in the current server.",
                                                                                      this::onInfo))

    private val serverManagers = mutableMapOf<String, ServerNewsManager>()

    private fun getManager(server: Server) = serverManagers[server.id]!!

    override fun onConnect(tx: PersistenceTransaction) {
        for (server in bot.discord.servers) {
            val serverManager = ServerNewsManager(server.id,
                                                  ArrayList(tx.getDao(NewsConfigDao::class.java).findByServerId(
                                                          server.id)))
            add(serverManager)
        }
        thread {
            while (!bot.stop) {
                try {
                    logger.trace("Sleeping")
                    Thread.sleep(10000)
                    logger.trace("Fetching the news")
                    fetchNews()
                } catch (e: Exception) {
                    logger.error("Unexpected error", e)
                }
            }
        }
    }

    private fun fetchNews() {
        for (serverManager in serverManagers.values) {
            val server = bot.discord.getServerById(serverManager.serverId)
            logger.trace("Fetching the news for the server " + serverManager.serverId + " (" + server.name + ")")
            serverManager.fetchNews(bot.persistence, server)
        }
    }

    fun add(serverManager: ServerNewsManager) {
        serverManagers[serverManager.serverId] = serverManager
    }

    fun onAddSrc(message: Message, args: String?) {
        val splitArgs = args?.split(" ")?.toTypedArray() ?: arrayOf()
        var srcChannel: String? = splitArgs[0]
        val srcType = splitArgs[1]
        val srcData = splitArgs[2]

        srcChannel = message.channelReceiver.server.getChannelId(srcChannel!!)
        if (srcChannel == null) {
            message.reply("Could not add the source: could not find the channel " + splitArgs[0])
            return
        }
        try {
            val newsConfig = when (srcType) {
                "twitter" -> TwitterNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                else -> throw IllegalArgumentException(srcType + " is not a valid source type")
            }
            serverManagers[message.channelReceiver.server.id]!!.addSource(bot.persistence, newsConfig)
            message.reply("Added the source successfully")
        } catch (e: Exception) {
            logger.error("Could not add the source", e)
            message.reply("Could not add the source: " + e.message)
        }
    }

    private fun onInfo(message: Message, args: String?) {
        val server = message.channelReceiver.server
        getManager(server).showInfo(server, message)
    }

}
