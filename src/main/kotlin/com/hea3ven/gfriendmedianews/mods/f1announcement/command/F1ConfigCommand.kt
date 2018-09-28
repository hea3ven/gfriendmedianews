package com.hea3ven.gfriendmedianews.mods.f1announcement.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.f1announcement.F1AnnouncementModule
import com.hea3ven.gfriendmedianews.mods.f1announcement.dao.F1ServerConfigDao
import com.hea3ven.gfriendmedianews.util.getChannelId
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class F1ConfigCommand(private val mod: F1AnnouncementModule) :
        Command("f1config", "Configure the F1 announcements", requiresAdmin = true) {

    init {
        argParser.addArgument("channel")
        argParser.addArgument("mention_role")
    }

    override fun action(message: Message, args: Namespace) {
        val config = mod.serverConfigs[message.channelReceiver.server.id]!!
        config.enabled = true
        config.channel = message.channelReceiver.server.getChannelId(args.getString("channel"))
        config.mentionRole = args.getString("mention_role")
        mod.bot.persistence.beginTransaction().use { sess ->
            sess.getDao(F1ServerConfigDao::class.java).persist(config)
        }
    }

}