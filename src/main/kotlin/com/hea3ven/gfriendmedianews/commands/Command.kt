package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

interface Command {
	val name: String
	val requiresAdmin: Boolean
	val helpText: String
	fun handle(message: Message, cmdArgs: String?)
}

