package com.hea3ven.gfriendmedianews.mods.misc.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.misc.MiscModule
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class RektCommand(private val mod: MiscModule) : Command("rekt", "Show rekt message.") {
    override fun action(message: Message, args: Namespace) {
        val output = StringBuilder()
        output.append("Current status:\n")
        output.append(" ❎ Not **REKT**\n")
        output.append(" :white_check_mark: **REKT**\n")
        output.append(" :white_check_mark: **REKT**angle\n")
        output.append(" :white_check_mark: Sh**REKT**\n")
        output.append(" :white_check_mark: Tyrannosaurus **REKT**\n")
        output.append(" :white_check_mark: Star T**REKT**\n")
        output.append(" :white_check_mark: For**REKT** Gump\n")
        output.append(" :white_check_mark: E**REKT**ile dysfunction\n")
        output.append(" :white_check_mark: Shipw**REKT**\n")
        output.append(" :white_check_mark: Witness Prot**REKT**ion\n")
        output.append(" :white_check_mark: Close Encounters of the **REKT** kind\n")
        output.append(" :white_check_mark: Better Dead Than **REKT**\n")
        output.append(" :white_check_mark: Resur**REKT**\n")
        output.append(" :white_check_mark: Indi**REKT**\n")
        output.append(" :white_check_mark: Caught **REKT**handed\n")
        message.delete()
        message.reply(output.toString())
    }
}