package com.hea3ven.gfriendmedianews.mods.main.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.main.MainModule
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class StopCommand(private val mod: MainModule) :
        Command("stop", "Shuts down the bot.", requiresAdmin = true) {

    override fun action(message: Message, args: Namespace) {
        if (message.author.discriminator != "5336" && message.author.discriminator != "9116") {
            message.reply("You don't have permissions to do this")
            return
        }
        logger.info("Sending stop signal")
        mod.bot.stop = true
        message.reply("Goodbye!")
        logger.info("Disconnecting from discord")
        mod.bot.discord.disconnect()
        logger.info("Connection closed")
    }

}