package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

class ActionCommand(override val name: String, private val action: (Message, String?) -> Any) : Command {
	override fun handle(message: Message, cmdArgs: String?) {
		action(message, cmdArgs)
	}

}