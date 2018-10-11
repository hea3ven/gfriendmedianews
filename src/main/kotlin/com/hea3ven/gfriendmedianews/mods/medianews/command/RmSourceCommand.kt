package com.hea3ven.gfriendmedianews.mods.medianews.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.medianews.MediaNewsModule
import com.hea3ven.gfriendmedianews.mods.medianews.model.InstagramNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.model.TwitterNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.model.YouTubeNewsConfig
import com.hea3ven.gfriendmedianews.util.getChannelId
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class RmSourceCommand(private val mod: MediaNewsModule) :
        Command("rmsource", "Removes a news source.", requiresAdmin = true) {

    init {
        argParser.addArgument("channel")
        argParser.addArgument("type").choices("twitter", "instagram", "youtube")
        argParser.addArgument("data")
    }

    override fun action(message: Message, args: Namespace) {
        var srcChannel: String? = args.getString("channel")
        val srcType = args.getString("type")
        val srcData = args.getString("data")

        srcChannel = message.channelReceiver.server.getChannelId(srcChannel!!)
        if (srcChannel == null) {
            srcChannel = args.getString("channel")!!
        }

        try {
            val newsConfig = when (srcType) {
                "twitter" -> TwitterNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                "youtube" -> YouTubeNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                "instagram" -> InstagramNewsConfig(message.channelReceiver.server.id, srcChannel, srcData)
                else -> throw IllegalArgumentException("$srcType is not a valid source type")
            }
            mod.serverManagers[message.channelReceiver.server.id]!!.rmSource(mod.bot.persistence, newsConfig)
            message.reply("Removed the source successfully")
        } catch (e: Exception) {
            logger.error("Could not remove the source", e)
            message.reply("Could not remove the source: " + e.message)
        }
    }
}