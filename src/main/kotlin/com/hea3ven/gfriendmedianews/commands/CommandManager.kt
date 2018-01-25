package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.MessageCreateListener
import org.slf4j.LoggerFactory

class CommandManager(val prefix: String) : MessageCreateListener {
	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.CommandManager")

	private val commands: MutableMap<String, Command> = mutableMapOf()

	fun registerCommand(command :Command){
		commands.put(command.name, command)
	}

	override fun onMessageCreate(discord: DiscordAPI, message: Message) {
		logger.trace("Received message")
		if (!message.content.startsWith(prefix)) {
			return
		}
		val cmdLine = message.content.substring(prefix.length)
		val (cmdName, cmdArgs) = parseCmdLine(cmdLine)
		val command = commands[cmdName] ?: return
		logger.debug("Running command {}", command.name)
		command.handle(message, cmdArgs)
	}

	private fun parseCmdLine(cmdLine: String): Pair<String, String?> {
		val cmdNameEnd = cmdLine.indexOf(' ')
		if (cmdNameEnd != -1) {
			return Pair(cmdLine.substring(0, cmdNameEnd), cmdLine.substring(cmdNameEnd + 1))
		} else {
			return Pair(cmdLine, null)
		}
	}
}