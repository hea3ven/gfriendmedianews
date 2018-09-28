package com.hea3ven.gfriendmedianews.mods.socialinteraction.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.socialinteraction.SocialInteractionsModule
import com.hea3ven.gfriendmedianews.mods.socialinteraction.dao.SocialInteractionDao
import com.hea3ven.gfriendmedianews.mods.socialinteraction.model.InteractionType
import com.hea3ven.gfriendmedianews.mods.socialinteraction.model.SocialInteractionStat
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class InteractionCommand(private val mod: SocialInteractionsModule, val type: InteractionType) :
        Command(type.command, type.help) {

    init {
        argParser.addArgument("target").nargs("+")
    }

    override fun action(message: Message, args: Namespace) {
        val targets = args.getList<String>("target")
        if (targets.any { it.contains("@everyone") || it.contains("@here") }) {
            message.reply(":eyes:")
            return
        }
        message.delete()
        val slapper = message.author.id
        val slappees = parseSlapees(targets)
        message.reply(message.author.mentionTag + " " + type.verb + " " + targets.joinToString(" "))
        mod.bot.persistence.beginTransaction().use { tx ->
            slappees.forEach {
                val slapStat = SocialInteractionStat(message.channelReceiver.server.id, message.channelReceiver.id,
                                                     type, slapper, it)
                tx.getDao(SocialInteractionDao::class.java).persist(slapStat)
            }
        }
    }

    private fun parseSlapees(args: List<String>) = args.mapNotNull {
        if (it.startsWith("<@") && it.endsWith(">")) it.substring(2, it.length - 1) else null
    }

}