package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.entities.message.Message

class SocialInteractionsModule(private val persistence: Persistence) : Module {
	override val commands = listOf(ActionCommand("slap", this::onSlap),
			ActionCommand("slapStat", this::onSlapStat))

	init{

		persistence.registerDaoFactory(SocialInteractionDao::class.java, SocialInteractionDaoFactory())
	}

	fun onSlap(message: Message, args: String?) {
		if(args == null) {
			return
		}
		message.delete()
		val slapper = message.author.id
		val slappees = parseSlapees(args.split(" "))
		message.reply(message.author.mentionTag + " slapped " + args)
		persistence.beginTransaction().use { tx ->
			slappees.forEach {
				val slapStat = tx.getDao(SocialInteractionDao::class.java).find(slapper, it) ?: SocialInteractionStat(
						slapper, it)
				slapStat.count += 1
				tx.getDao(SocialInteractionDao::class.java).persist(slapStat)
			}
		}
	}

	private fun parseSlapees(args: List<String>) = args.mapNotNull {
		if (it.startsWith("<@") && it.endsWith(">")) it.substring(2, it.length - 2) else null
	}

	fun onSlapStat(message: Message, args: String?) {
		persistence.beginTransaction().use { tx ->
			val slapper = message.author.id
			val slapperTimes = tx.getDao(SocialInteractionDao::class.java).countTimesSlapper(slapper)
			val slappeeTimes = tx.getDao(SocialInteractionDao::class.java).countTimesSlappee(slapper)
			message.reply("You've slapped $slapperTimes times")
			message.reply("You've been slapped $slappeeTimes times")
		}
	}

}

