package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.commands.CommandManager
import com.hea3ven.gfriendmedianews.domain.ServerConfig
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class MediaNews(val persistence: Persistence, val discord: DiscordAPI) {

	private val serverManagers = mutableMapOf<String, ServerNewsManager>()

	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.MediaNews")

	private var stop = false

	private val commandManager = CommandManager.builder()
			.addCommand("\$help", { message, args -> onHelp(message) })
			.addCommand("\$info", { message, args -> onInfo(message) })
			.addCommand("\$stop", { message, args -> onStop(message) })
			.addCommand("\$slap", { message, args -> onSlap(message, args) })
			.addCommand("\$setchannel", { message, args -> onSetChannel(message, args) })
			.addCommand("\$addsource", { message, args -> onAddSrc(message, args) })
			.build()

	fun start() {
		logger.info("Connecting to discord")
		discord.connectBlocking()
		logger.info("Connecting established")
		onConnect()
		while (!stop) {
			logger.trace("Sleeping")
			Thread.sleep(10000)
			logger.trace("Fetching the news")
			fetchNews()
		}
		Thread.sleep(5000)
		logger.info("Disconnecting from discord")
		discord.disconnect()
		logger.info("Connection closed")
	}

	private fun fetchNews() {
		for (serverManager in serverManagers.values) {
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
		output.append(" **\$setchannel [channel]**: Sets the channel where the news will be posted.")
		output.append(" This command requires administrator priviledges.\n")
		output.append(" **\$addsource [type] [username]**: Adds a news source.")
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
		message.reply("Goodbye!")
		stop = true
		System.exit(1)
	}

	fun onSlap(message: Message, args: Array<String>) {
		message.delete()
		message.reply(message.author.mentionTag + " slapped " + args.joinToString())
	}

	fun onSetChannel(message: Message, args: Array<String>) {
		if (!isAdmin(message.channelReceiver.server, message.author)) {
			message.reply("You don't have permissions to do this")
			return
		}
		val channel: Channel?
		if (args[0].startsWith("<#")) {
			channel = message.channelReceiver.server.getChannelById(args[0].substring(2, args[0].length - 1))
		} else {
			channel = message.channelReceiver.server.channels.find { it.name == args[0] }
		}
		if (channel != null) {
			serverManagers[message.channelReceiver.server.id]!!.setChannel(persistence, channel.id)
			message.reply("The news channel has been set as <#" + channel.id + ">")
		} else {

			message.reply("Could not find channel #" + args[0])
		}
	}

	fun onAddSrc(message: Message, args: Array<String>) {
		if (!isAdmin(message.channelReceiver.server, message.author)) {
			message.reply("You don't have permissions to do this")
			return
		}
		val srcType = args[0]
		val srcData = args[1]
		try {
			serverManagers[message.channelReceiver.server.id]!!.addSource(persistence, srcType, srcData)
		} catch (e: Exception) {
			message.reply("Could not add the source: " + e.message)
		}
	}

	private fun isAdmin(server: Server, author: User): Boolean {
		val botRolePos = discord.yourself.getRoles(server).filter { it.hoist }.map { it.position }.min()
		val roles = author.getRoles(server)
		return !(roles.isEmpty() || roles.all { it.position < botRolePos!! })
	}

	private fun getManager(server: Server) = serverManagers[server.id]!!

}