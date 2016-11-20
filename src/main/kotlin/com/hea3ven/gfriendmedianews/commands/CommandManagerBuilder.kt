package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message

class CommandManagerBuilder {
	private val commands: MutableSet<Command> = mutableSetOf()

	fun addCommand(commandName: String,
			commandAction: (Message, Array<String>) -> Any): CommandManagerBuilder {
		commands.add(Command(commandName, commandAction))
		return this
	}

	fun build(): CommandManager {
		return CommandManager(commands)
	}

}