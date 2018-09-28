package com.hea3ven.gfriendmedianews.mods.medianews.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.medianews.MediaNewsModule
import com.hea3ven.gfriendmedianews.mods.medianews.model.InstagramNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.model.TwitterNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.model.YouTubeNewsConfig
import com.hea3ven.gfriendmedianews.util.getChannelId
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class AddSourceCommand(private val mod: MediaNewsModule) :
        Command("addsource", "Adds a news source.", requiresAdmin = true) {

    init {
        argParser.addArgument("channel").choices("twitter", "instagram", "youtube")
        argParser.addArgument("type")
        argParser.addArgument("data")
    }

    override fun action(message: Message, args: Namespace) {
        var srcChannel: String? = args.getString("channel")
        val srcType = args.getString("type")
        val srcData = args.getString("data")

        srcChannel = message.channelReceiver.server.getChannelId(srcChannel!!)
        if (srcChannel == null) {
            message.reply("Could not add the source: could not find the channel " + args.getString("channel"))
            return
        }
        try {
            val newsConfig = when (srcType) {
                "twitter" -> TwitterNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                "youtube" -> YouTubeNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                "instagram" -> InstagramNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                else -> throw IllegalArgumentException("$srcType is not a valid source type")
            }
            mod.serverManagers[message.channelReceiver.server.id]!!.addSource(mod.bot.persistence, newsConfig)
            message.reply("Added the source successfully")
        } catch (e: Exception) {
            logger.error("Could not add the source", e)
            message.reply("Could not add the source: " + e.message)
        }
    }
}