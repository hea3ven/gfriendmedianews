package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

class ActionCommand(override val name: String, override val helpText: String,
		private val action: (Message, String?) -> Any, override val requiresAdmin: Boolean = false) : Command {
	override fun handle(message: Message, cmdArgs: String?) {
		action(message, cmdArgs)
	}
}