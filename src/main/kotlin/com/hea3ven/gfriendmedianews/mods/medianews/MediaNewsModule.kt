package com.hea3ven.gfriendmedianews.mods.medianews

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.ServerNewsManager
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.medianews.command.AddSourceCommand
import com.hea3ven.gfriendmedianews.mods.medianews.command.MediaInfoCommand
import com.hea3ven.gfriendmedianews.mods.medianews.command.RmSourceCommand
import com.hea3ven.gfriendmedianews.mods.medianews.dao.NewsConfigDao
import com.hea3ven.gfriendmedianews.mods.medianews.dao.ServerConfigDaoFactory
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import de.btobastian.javacord.entities.Server
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class MediaNewsModule(val bot: ChinguBot) : Module {

    init {
        bot.persistence.registerDaoFactory(NewsConfigDao::class.java, ServerConfigDaoFactory())
    }

    private val logger = LoggerFactory.getLogger(MediaNewsModule::class.java)

    override val commands = listOf(AddSourceCommand(this), RmSourceCommand(this), MediaInfoCommand(this))

    internal val serverManagers = mutableMapOf<String, ServerNewsManager>()

    internal fun getManager(server: Server) = serverManagers[server.id]!!

    override fun onConnect(tx: PersistenceTransaction) {
        for (server in bot.discord.servers) {
            val serverManager = ServerNewsManager(server.id, ArrayList(
                    tx.getDao(NewsConfigDao::class.java).findByServerId(server.id)))
            serverManagers[serverManager.serverId] = serverManager
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

}
