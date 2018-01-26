package com.hea3ven.gfriendmedianews

import com.hea3ven.gfriendmedianews.mods.main.MainModule
import com.hea3ven.gfriendmedianews.mods.medianews.MediaNewsModule
import com.hea3ven.gfriendmedianews.mods.misc.MiscModule
import com.hea3ven.gfriendmedianews.mods.socialinteraction.SocialInteractionsModule
import com.hea3ven.gfriendmedianews.persistance.Persistence
import com.hea3ven.gfriendmedianews.util.DiscordBot
import de.btobastian.javacord.DiscordAPI

class ChinguBot(persistence: Persistence, discord: DiscordAPI) : DiscordBot(persistence, discord) {

	override val modules = listOf(MainModule(this), MiscModule(), SocialInteractionsModule(persistence),
			MediaNewsModule(this))

}

