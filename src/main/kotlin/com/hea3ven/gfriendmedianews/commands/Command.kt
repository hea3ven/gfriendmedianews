package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

interface Command {
	val name: String
	fun handle(message: Message, cmdArgs: String?)

}

