package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.entities.message.Message

class SocialInteractionsModule(private val persistence: Persistence) : Module {
    override val commands = listOf(ActionCommand("slap", " **\$slap [target]**: Slap the target.",
                                                 { m, a -> onInteraction(m, a, InteractionType.SLAP) }),
                                   ActionCommand("slapstat", " **\$slapstat**: Show statistics of your slaps.",
                                                 { m, a -> onInteractionStat(m, a, InteractionType.SLAP) }),
                                   ActionCommand("hug", " **\$hug [target]**: Hug the target.",
                                                 { m, a -> onInteraction(m, a, InteractionType.HUG) }),
                                   ActionCommand("hugstat", " **\$hugstat**: Show statistics of your hugs.",
                                                 { m, a -> onInteractionStat(m, a, InteractionType.HUG) }))

    init {

        persistence.registerDaoFactory(SocialInteractionDao::class.java, SocialInteractionDaoFactory())
    }

    fun onInteraction(message: Message, args: String?, type: InteractionType) {
        if (args == null) {
            return
        }
        if (args.contains("@everyone") || args.contains("@here")) {
            message.reply(":eyes:")
            return
        }
        message.delete()
        val slapper = message.author.id
        val slappees = parseSlapees(args.split(" "))
        message.reply(message.author.mentionTag + " " + type.verb + " " + args)
        persistence.beginTransaction().use { tx ->
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

    fun onInteractionStat(message: Message, args: String?, type: InteractionType) {
        persistence.beginTransaction().use { tx ->
            val slapper = args?.substring(2, args.length - 1) ?: message.author.id
            val slapperTimes = tx.getDao(SocialInteractionDao::class.java).countTimesSource(type, slapper)
            val slappeeTimes = tx.getDao(SocialInteractionDao::class.java).countTimesTarget(type, slapper)
            val slapperMember = message.channelReceiver.server.getMemberById(slapper)!!
            message.reply(
                    "${slapperMember.name}:\n\tYou've ${type.verb} $slapperTimes time(s)\n\tYou've been ${type.verb} $slappeeTimes time(s)")
        }
    }

}

