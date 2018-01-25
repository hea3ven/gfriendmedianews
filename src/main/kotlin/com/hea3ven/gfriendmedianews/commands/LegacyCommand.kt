package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

class LegacyCommand(override val name: String, private val action: (Message, Array<String>) -> Any) : Command {
	override fun handle(message: Message, cmdArgs: String?) {
		action(message, cmdArgs?.split(" ")?.toTypedArray() ?: arrayOf())
	}

}