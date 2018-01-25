package com.hea3ven.gfriendmedianews.mods.medianews

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.ServerNewsManager
import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.commands.LegacyCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import com.hea3ven.gfriendmedianews.util.isAdmin
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class MediaNewsModule(val bot: ChinguBot) : Module {

	init{
		bot.persistence.registerDaoFactory(SourceConfigDao::class.java, SourceConfigDaoFactory())
		bot.persistence.registerDaoFactory(ServerConfigDao::class.java, ServerConfigDaoFactory())
	}

	private val logger = LoggerFactory.getLogger(MediaNewsModule::class.java)

	override val commands = listOf(LegacyCommand("addsource", this::onAddSrc),
			ActionCommand("info", this::onInfo))

	private val serverManagers = mutableMapOf<String, ServerNewsManager>()

	private fun getManager(server: Server) = serverManagers[server.id]!!

	override fun onConnect(tx: PersistenceTransaction) {
		for (server in bot.discord.servers) {
			var serverConfig = tx.getDao(ServerConfigDao::class.java).findByServerId(server.id)
			if (serverConfig == null) {
				serverConfig = ServerConfig(serverId = server.id!!)
				tx.getDao(ServerConfigDao::class.java).persist(serverConfig)
			}
			val serverManager = ServerNewsManager(serverConfig)
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
			logger.trace("Fetching the news for the server " + serverManager.serverConfig.serverId)
			serverManager.fetchNews(bot.persistence, bot.discord)
		}
	}

	fun add(serverManager: ServerNewsManager) {
		serverManagers.put(serverManager.serverConfig.serverId, serverManager)
	}

	fun onAddSrc(message: Message, args: Array<String>) {
		if (!message.channelReceiver.server.isAdmin(bot, message.author)) {
			message.reply("You don't have permissions to do this")
			return
		}
		var srcChannel: String? = args[0]
		val srcType = args[1]
		val srcData = args[2]

		if (srcChannel!!.startsWith("<#")) {
			srcChannel = message.channelReceiver.server.getChannelById(
					srcChannel.substring(2, srcChannel.length - 1)).id
		} else {
			srcChannel = message.channelReceiver
					.server
					.channels
					.find { it.name == srcChannel }
					?.id
		}
		if (srcChannel == null) {
			message.reply("Could not add the source: could not find the channel " + args[0])
			return
		}
		try {
			serverManagers[message.channelReceiver.server.id]!!.addSource(bot.persistence, srcType, srcData,
					srcChannel)
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