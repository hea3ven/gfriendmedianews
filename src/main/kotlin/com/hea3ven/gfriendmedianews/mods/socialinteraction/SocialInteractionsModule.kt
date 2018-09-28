package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.socialinteraction.command.InteractionCommand
import com.hea3ven.gfriendmedianews.mods.socialinteraction.command.InteractionInfoCommand
import com.hea3ven.gfriendmedianews.mods.socialinteraction.dao.SocialInteractionDao
import com.hea3ven.gfriendmedianews.mods.socialinteraction.dao.SocialInteractionDaoFactory
import com.hea3ven.gfriendmedianews.mods.socialinteraction.model.InteractionType
import com.hea3ven.gfriendmedianews.util.DiscordBot

class SocialInteractionsModule(val bot: DiscordBot) : Module {

    override val commands = listOf(InteractionCommand(this, InteractionType.SLAP),
                                   InteractionInfoCommand(this, InteractionType.SLAP),
                                   InteractionCommand(this, InteractionType.HUG),
                                   InteractionInfoCommand(this, InteractionType.HUG))

    init {
        bot.persistence.registerDaoFactory(SocialInteractionDao::class.java, SocialInteractionDaoFactory())
    }

}

