package com.hea3ven.gfriendmedianews.mods.permissions

import com.hea3ven.gfriendmedianews.commands.PermissionManager
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDao
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User

class DbPermissionManager(val persistence: Persistence) : PermissionManager {

    override fun hasPermission(user: User, server: Server, discord: DiscordAPI, key: String): Boolean {
        persistence.beginTransaction().use {
            val permissions = it.getDao(UserPermissionsDao::class.java).findByUserId(user)
            return permissions?.getPermission(server, key) ?: false
        }
    }

}
