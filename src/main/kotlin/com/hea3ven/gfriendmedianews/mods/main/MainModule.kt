package com.hea3ven.gfriendmedianews.mods.main

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.main.command.HelpCommand
import com.hea3ven.gfriendmedianews.mods.main.command.StopCommand

class MainModule(internal val bot: ChinguBot) : Module {

    override val commands = listOf(HelpCommand(this), StopCommand(this))

}
