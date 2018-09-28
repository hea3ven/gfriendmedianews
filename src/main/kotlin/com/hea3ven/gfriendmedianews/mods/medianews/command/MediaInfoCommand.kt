package com.hea3ven.gfriendmedianews.mods.medianews.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.medianews.MediaNewsModule
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class MediaInfoCommand(private val mod: MediaNewsModule) :
        Command("mediainfo", "Show the configuration and information of the media in the current server.") {

    override fun action(message: Message, args: Namespace) {
        val server = message.channelReceiver.server
        mod.getManager(server).showInfo(server, message)
    }
}