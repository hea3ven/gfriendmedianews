package com.hea3ven.gfriendmedianews.mods.main

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.util.isAdmin
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class MainModule(private val bot: ChinguBot) : Module {

	private val logger = LoggerFactory.getLogger(MainModule::class.java)

	override val commands = listOf(ActionCommand("help", " **\$help**: Show this help message.", this::onHelp),
			ActionCommand("stop", " **\$stop**: Shuts down the bot.", this::onStop, true))

	fun onHelp(message: Message, args: String?) {
		val output = StringBuilder()
		output.append("This is BuddyBot, by <@173217833168273408>.\n")
		output.append(" The available commands are the following:\n")
		bot.modules.flatMap(Module::commands).filter { c ->
			!c.requiresAdmin || message.channelReceiver.server.isAdmin(bot.discord, message.author)
		}.forEach({ c -> output.append(c.helpText).append('\n') })
		message.reply(output.toString())
	}

	fun onStop(message: Message, args: String?) {
		if (message.author.discriminator != "5336" && message.author.discriminator != "9116") {
			message.reply("You don't have permissions to do this")
			return
		}
		logger.info("Sending stop signal")
		bot.stop = true
		message.reply("Goodbye!")
		logger.info("Disconnecting from discord")
		bot.discord.disconnect()
		logger.info("Connection closed")
	}

}
