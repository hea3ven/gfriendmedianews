package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.entities.message.Message

class SocialInteractionsModule(private val persistence: Persistence) : Module {
	override val commands = listOf(ActionCommand("slap", { m, a -> onInteraction(m, a, InteractionType.SLAP) }),
			ActionCommand("slapstat", { m, a -> onInteractionStat(m, a, InteractionType.SLAP) }),
			ActionCommand("hug", { m, a -> onInteraction(m, a, InteractionType.HUG) }),
			ActionCommand("hugstat", { m, a -> onInteractionStat(m, a, InteractionType.HUG) }))

	init {

		persistence.registerDaoFactory(SocialInteractionDao::class.java, SocialInteractionDaoFactory())
	}

	fun onInteraction(message: Message, args: String?, type: InteractionType) {
		if (args == null) {
			return
		}
		message.delete()
		val slapper = message.author.id
		val slappees = parseSlapees(args.split(" "))
		message.reply(message.author.mentionTag + " slapped " + args)
		persistence.beginTransaction().use { tx ->
			slappees.forEach {
				val slapStat = SocialInteractionStat(type, slapper, it)
				tx.getDao(SocialInteractionDao::class.java).persist(slapStat)
			}
		}
	}

	private fun parseSlapees(args: List<String>) = args.mapNotNull {
		if (it.startsWith("<@") && it.endsWith(">")) it.substring(2, it.length - 2) else null
	}

	fun onInteractionStat(message: Message, args: String?, type: InteractionType) {
		persistence.beginTransaction().use { tx ->
			val slapper = message.author.id
			val slapperTimes = tx.getDao(SocialInteractionDao::class.java).countTimesSource(type, slapper)
			val slappeeTimes = tx.getDao(SocialInteractionDao::class.java).countTimesTarget(type, slapper)
			message.reply("You've ${type.verb} $slapperTimes time(s)\nYou've been ${type.verb} $slappeeTimes time(s)")
		}
	}

}

