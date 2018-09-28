package com.hea3ven.gfriendmedianews.mods.socialinteraction.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.socialinteraction.SocialInteractionsModule
import com.hea3ven.gfriendmedianews.mods.socialinteraction.dao.SocialInteractionDao
import com.hea3ven.gfriendmedianews.mods.socialinteraction.model.InteractionType
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class InteractionInfoCommand(private val mod: SocialInteractionsModule, val type: InteractionType) :
        Command(type.command + "stat", "Show statistics of your ${type.command}s") {

    init {
        argParser.addArgument("user").setDefault(null)
    }

    override fun action(message: Message, args: Namespace) {
        mod.bot.persistence.beginTransaction().use { tx ->
            val arg = args.getString("user")
            val slapper = arg?.substring(2, arg.length - 1) ?: message.author.id
            val slapperTimes = tx.getDao(SocialInteractionDao::class.java).countTimesSource(type, slapper)
            val slappeeTimes = tx.getDao(SocialInteractionDao::class.java).countTimesTarget(type, slapper)
            val slapperMember = message.channelReceiver.server.getMemberById(slapper)!!
            message.reply(
                    "${slapperMember.name}:\n\tYou've ${type.verb} $slapperTimes time(s)\n\tYou've been ${type.verb} $slappeeTimes time(s)")
        }
    }

}