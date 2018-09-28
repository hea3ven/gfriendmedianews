package com.hea3ven.gfriendmedianews.mods.permissions

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.permissions.command.PermissionCommand
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDao
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDaoFactory
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction

class PermissionsModule(val bot: ChinguBot) : Module {


    override val commands = listOf(PermissionCommand(this))

    init {
        bot.persistence.registerDaoFactory(UserPermissionsDao::class.java, UserPermissionsDaoFactory())
    }

    override fun onConnect(tx: PersistenceTransaction) {
        super.onConnect(tx)
        bot.commandManager.permissionManager = DbPermissionManager(bot.persistence)
    }

}
