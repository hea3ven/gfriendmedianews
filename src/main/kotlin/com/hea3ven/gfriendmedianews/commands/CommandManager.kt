package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.MessageCreateListener
import org.slf4j.LoggerFactory

class CommandManager(val commands: Set<Command>) : MessageCreateListener {
	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.CommandManager")

	fun init(discord: DiscordAPI) {
		discord.registerListener(this)
	}

	override fun onMessageCreate(discord: DiscordAPI?, message: Message?) {
		logger.trace("Received message")
		commands.filter { it.handle(message!!) }.forEach { return }
	}

	companion object {
		fun builder() = CommandManagerBuilder()
	}
}