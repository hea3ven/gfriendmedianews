package com.hea3ven.gfriendmedianews.mods.main

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class MainModule(private val bot: ChinguBot) : Module {

	private val logger = LoggerFactory.getLogger(MainModule::class.java)

	override val commands = listOf(ActionCommand("help", this::onHelp),
			ActionCommand("stop", this::onStop))

	fun onHelp(message: Message, args: String?) {
		val output = StringBuilder()
		output.append("This is BuddyBot, by <@173217833168273408>.\n")
		output.append(" The available commands are the following:\n")
		output.append(" **\$help**: Show this help message.\n")
		output.append(" **\$info**: Show the configuration and information of the current server.\n")
		output.append(" **\$addsource [channel] [type] [username]**: Adds a news source.")
		output.append(" This command requires administrator priviledges.\n")
		output.append("    **type** can be one of:\n")
		output.append("        \\* twitter\n")
		output.append("        \\* instagram\n")
		output.append("        \\* youtube\n")
		output.append("    examples:\n")
		output.append("        \\* \$addsource twitter @GFRDOfficial\n")
		output.append("        \\* \$addsource instagram gfriendofficial\n")
		output.append("        \\* \$addsource youtube gfrdofficial\n")
		output.append(" **\$slap [target]**: Slap the target.\n")
		output.append(" **\$rekt**: Show rekt message.\n")
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
