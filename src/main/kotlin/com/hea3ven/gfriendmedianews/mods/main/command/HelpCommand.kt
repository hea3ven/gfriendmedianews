package com.hea3ven.gfriendmedianews.mods.main.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.main.MainModule
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class HelpCommand(private val mod: MainModule) : Command("help", "Show this help message.") {

    override fun action(message: Message, args: Namespace) {
        val output = StringBuilder()
        output.append("This is BuddyBot, by <@173217833168273408>.\n")
        output.append(" The available commands are the following:\n")
        mod.bot.modules.flatMap(Module::commands).filter { c ->
            mod.bot.commandManager.permissionManager.hasPermission(message.author, message.channelReceiver.server,
                                                                   mod.bot.discord, c)
        }.forEach { c -> output.append("   " + c.getHelpSummary(mod.bot.commandManager)).append('\n') }
        message.reply(output.toString())
    }

}