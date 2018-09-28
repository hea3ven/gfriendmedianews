package com.hea3ven.gfriendmedianews.mods.misc

import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.misc.command.RektCommand

class MiscModule : Module {

    override val commands = listOf(RektCommand(this))

}

