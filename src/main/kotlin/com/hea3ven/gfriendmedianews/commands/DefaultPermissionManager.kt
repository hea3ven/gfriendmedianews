package com.hea3ven.gfriendmedianews.commands

import com.hea3ven.gfriendmedianews.util.isAdmin
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User

class DefaultPermissionManager : PermissionManager {

    override fun hasPermission(user: User, server: Server, discord: DiscordAPI, key: String): Boolean {
        return true
    }

}
