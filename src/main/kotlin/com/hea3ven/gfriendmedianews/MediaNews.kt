package com.hea3ven.gfriendmedianews

import com.google.common.util.concurrent.FutureCallback
import com.hea3ven.gfriendmedianews.commands.CommandManager
import com.hea3ven.gfriendmedianews.domain.ServerConfig
import com.hea3ven.gfriendmedianews.domain.SlapStat
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.server.ServerJoinListener
import de.btobastian.javacord.listener.server.ServerLeaveListener
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class MediaNews(val persistence: Persistence, val discord: DiscordAPI) {

	private val serverManagers = mutableMapOf<String, ServerNewsManager>()

	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.MediaNews")

	private var stop = false

	private val commandManager = CommandManager.builder()
			.addCommand("\$help", { message, args -> onHelp(message) })
			.addCommand("\$info", { message, args -> onInfo(message) })
			.addCommand("\$stop", { message, args -> onStop(message) })
			.addCommand("\$slap", { message, args -> onSlap(message, args) })
			.addCommand("\$rekt", { message, args -> onRekt(message) })
			.addCommand("\$addsource", { message, args -> onAddSrc(message, args) })
			.build()

	fun start() {
		logger.info("Connecting to discord")
		discord.setAutoReconnect(false)
		discord.connect(object : FutureCallback<DiscordAPI> {
			override fun onSuccess(result: DiscordAPI?) {
				logger.info("Connection established")
				onConnect()
				thread {
					while (!stop) {
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

			override fun onFailure(t: Throwable) {
				logger.error("Could not connect", t)
			}

		})
		while (!stop) {
			Thread.sleep(5000)
		}
	}

	private fun fetchNews() {
		for (serverManager in serverManagers.values) {
			logger.trace("Fetching the news for the server " + serverManager.serverConfig.serverId)
			serverManager.fetchNews(persistence, discord)
		}
	}

	fun onConnect() {
		persistence.beginTransaction().use { tx ->
			for (server in discord.servers) {
				var serverConfig = tx.serverConfigDao.findByServerId(server.id)
				if (serverConfig == null) {
					serverConfig = ServerConfig(serverId = server.id!!)
					tx.serverConfigDao.persist(serverConfig)
				}
				val serverManager = ServerNewsManager(serverConfig)
				add(serverManager)
			}
		}
		commandManager.init(discord)
		discord.registerListener(object : ServerJoinListener, ServerLeaveListener {
			override fun onServerJoin(api: DiscordAPI, server: Server) {
				logger.info("Joined to {}", server.name)
			}

			override fun onServerLeave(api: DiscordAPI, server: Server) {
				logger.info("Leaved to {}", server.name)
			}

		})
		discord.setGame("with buddies (\$help)")
	}

	fun add(serverManager: ServerNewsManager) {
		serverManagers.put(serverManager.serverConfig.serverId, serverManager)
	}

	fun onHelp(message: Message) {
		val output = StringBuilder()
		output.append("This is BuddyBot, by <@173217833168273408>.\n")
		output.append(" The available commands are the following:\n")
		output.append(" **\$help**: Show this help message.\n")
		output.append(" **\$info**: Show the configuration and information of the current server.\n")
		output.append(" **\$addsource [channel] [type] [username]**: Adds a news source.")
		output.append(" This command requires administrator priviledges.\n")
		output.append("    **type** can be one of:\n")
		output.append("        \\* twitter\n")
		output.append("        \\* instagram\n")
		output.append("        \\* youtube\n")
		output.append("    examples:\n")
		output.append("        \\* \$addsource twitter @GFRDOfficial\n")
		output.append("        \\* \$addsource instagram gfriendofficial\n")
		output.append("        \\* \$addsource youtube gfrdofficial\n")
		output.append(" **\$slap [target]**: Slap the target.\n")
		output.append(" **\$rekt**: Show rekt message.\n")
		message.reply(output.toString())
	}

	private fun onInfo(message: Message) {
		val server = message.channelReceiver.server
		getManager(server).showInfo(server, message)
	}

	fun onStop(message: Message) {
		if (message.author.discriminator != "5336" && message.author.discriminator != "9116") {
			message.reply("You don't have permissions to do this")
			return
		}
		logger.info("Sending stop signal")
		stop = true
		message.reply("Goodbye!")
		logger.info("Disconnecting from discord")
		discord.disconnect()
		logger.info("Connection closed")
	}

	fun onSlap(message: Message, args: Array<String>) {
		message.delete()
		val slapper = message.author.id
		val slappees = parseSlapees(args)
		message.reply(message.author.mentionTag + " slapped " + args.joinToString())
		persistence.beginTransaction().use { tx ->
			slappees.forEach {
				val slapStat = tx.slapStatDao.find(slapper, it) ?: SlapStat(slapper, it)
				if (slapStat != null) {
					slapStat.count += 1
					tx.slapStatDao.persist(slapStat)
				}
			}
		}
	}

	private fun parseSlapees(args: Array<String>) = args.mapNotNull {
		if (it.startsWith("<@") && it.endsWith(">")) it.substring(2, it.length - 2) else null
	}

	fun onSlapStat(message: Message, args: Array<String>) {
		persistence.beginTransaction().use { tx ->
			val slapper = message.author.id
			val slapperTimes = tx.slapStatDao.countTimesSlapper(slapper)
			val slappeeTimes = tx.slapStatDao.countTimesSlappee(slapper)
			message.reply("You've slapped $slapperTimes times")
			message.reply("You've been slapped $slappeeTimes times")
		}
	}

	fun onAddSrc(message: Message, args: Array<String>) {
		if (!isAdmin(message.channelReceiver.server, message.author)) {
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
			serverManagers[message.channelReceiver.server.id]!!.addSource(persistence, srcType, srcData,
					srcChannel)
			message.reply("Added the source successfully")
		} catch (e: Exception) {
			logger.error("Could not add the source", e)
			message.reply("Could not add the source: " + e.message)
		}
	}

	fun onRekt(message: Message) {
		val output = StringBuilder()
		output.append("Current status:\n")
		output.append(" ❎ Not **REKT**\n")
		output.append(" :white_check_mark: **REKT**\n")
		output.append(" :white_check_mark: **REKT**angle\n")
		output.append(" :white_check_mark: Sh**REKT**\n")
		output.append(" :white_check_mark: Tyrannosaurus **REKT**\n")
		output.append(" :white_check_mark: Star T**REKT**\n")
		output.append(" :white_check_mark: For**REKT** Gump\n")
		output.append(" :white_check_mark: E**REKT**ile disfunction\n")
		output.append(" :white_check_mark: Shipw**REKT**\n")
		output.append(" :white_check_mark: Witness Prot**REKT**ion\n")
		output.append(" :white_check_mark: Close Encounters of the **REKT** kind\n")
		output.append(" :white_check_mark: Better Dead Than **REKT**\n")
		output.append(" :white_check_mark: Resur**REKT**\n")
		output.append(" :white_check_mark: Indi**REKT**\n")
		output.append(" :white_check_mark: Caught **REKT**handed\n")
		message.delete()
		message.reply(output.toString())
	}

	private fun isAdmin(server: Server, author: User): Boolean {
		val botRolePos = discord.yourself.getRoles(server).filter { it.hoist }.map { it.position }.min()
		val roles = author.getRoles(server)
		return !(roles == null || roles.isEmpty() || roles.all { it.position < botRolePos!! })
	}

	private fun getManager(server: Server) = serverManagers[server.id]!!

}