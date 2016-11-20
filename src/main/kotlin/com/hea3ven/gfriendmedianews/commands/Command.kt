package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class Command(val commandName: String, val commandAction: (Message, Array<String>) -> Any) {
	private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.Command")

	fun handle(message: Message): Boolean {
		if (message.content.startsWith(commandName)) {
			logger.debug("Running command {}", commandName)
			val args = message.content.substring(commandName.length).trimStart().split(" ")
			commandAction(message, args.toTypedArray())
			return true
		}
		return false
	}

}