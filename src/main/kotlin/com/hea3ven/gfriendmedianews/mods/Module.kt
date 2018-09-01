package com.hea3ven.gfriendmedianews.mods

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction

interface Module {

    val commands: List<Command>
    fun onConnect(tx: PersistenceTransaction) {
    }

}